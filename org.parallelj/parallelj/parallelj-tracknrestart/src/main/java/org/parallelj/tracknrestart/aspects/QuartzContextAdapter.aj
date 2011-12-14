package org.parallelj.tracknrestart.aspects;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.parallelj.Programs;
import org.parallelj.Programs.ProcessHelper;
import org.parallelj.internal.kernel.KCall;
import org.parallelj.internal.kernel.KProcess;
import org.parallelj.internal.reflect.ProgramAdapter.Adapter;
import org.parallelj.tracknrestart.annotations.TrackNRestart;
import org.parallelj.tracknrestart.listeners.ForEachListener;
import org.parallelj.tracknrestart.plugins.TrackNRestartPlugin;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobPersistenceException;
import org.parallelj.tracknrestart.ReturnCodes;

privileged public aspect QuartzContextAdapter percflow (execution(public void Job+.execute(..) throws JobExecutionException)) {
	
	declare precedence :
		org.parallelj.tracknrestart.aspects.QuartzContextAdapter,
		org.parallelj.launching.quartz.JobsAdapter;

	static Logger logger = Logger.getLogger("org.parallelj.tracknrestart.QuartzContextAdapter");
	
	private X root;

	String result = ReturnCodes.SUCCESS.name();
	
	//---------------------------------------------------------------------------------------------------
	
	declare parents:
		(@org.parallelj.launching.QuartzExecution *) implements Job;

	public void Job.execute(JobExecutionContext context)
			throws JobExecutionException {
		
		ProcessHelper<?> p = null;
		logger.debug("-----------------------------------------------------------------------------------------------------");
		logger.debug("STARTING //J Root Program "+this.getClass().getName());

		p = Programs.as((Adapter) this);

		try {
			//TODO voir avec Christophe @IN
			Field f = this.getClass().getDeclaredField("data1");
			f.setAccessible(true);
			f.set(this, context.getJobDetail().getJobDataMap().get("data1"));
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		p.execute().join();

		logger.debug("ENDING   //J Root Program "+this.getClass().getName());
		logger.debug("-----------------------------------------------------------------------------------------------------");
		context.setResult(String.valueOf(p.getState()));
	}

	//---------------------------------------------------------------------------------------------------
	
	public interface X {};

	declare parents:           
		(org.quartz.Job+ && !org.quartz.Job) implements X;    

	declare parents:   
		(org.parallelj.internal.kernel.KCall) implements X;      

//	declare parents:   
//		(org.parallelj.mirror.Element+) implements X;      

//	declare parents:   
//		(org.parallelj.internal.kernel.callback.Entry+) implements X; 
	
//	declare parents:   
//		(org.parallelj.internal.kernel.callback.Exit+) implements X; 
	
	private ForEachListener X.forEachListener = null;       
	
	public ForEachListener X.getForEachListener() { 
		return forEachListener;
	}
	
	public void X.setForEachListener(ForEachListener forEachListener) {
		this.forEachListener = forEachListener;
	}
	
	private String X.restartedFireInstanceId = null;       
	
	public String X.getRestartedFireInstanceId() {
		return restartedFireInstanceId;
	}
	
	public void X.setRestartedFireInstanceId(String restartedFireInstanceId) {
		this.restartedFireInstanceId = restartedFireInstanceId;
	}
	
	//---------------------------------------------------------------------------------------------------
	
	//JobExecutionContext --> Job
	before(X self, JobExecutionContext jobExecutionContext): 
		execution(public void Job+.execute(..) throws JobExecutionException) 
		&& this(self) 
		&& args(jobExecutionContext) {

		//logger.debug("ASPECT BEFORE:execution(public void Job+.execute(..) throws JobExecutionException) && this(self) && args(jobExecutionContext)");

		self.setRestartedFireInstanceId(jobExecutionContext.getJobDetail().getJobDataMap().getString(TrackNRestartPlugin.RESTARTED_FIRE_INSTANCE_ID));
		self.setForEachListener((ForEachListener) jobExecutionContext.getJobDetail().getJobDataMap().get(TrackNRestartPlugin.FOR_EACH_LISTENER));
		//logger.debug("job="+self); 
		//logger.debug("restartedFireInstanceId="+self.getRestartedFireInstanceId()); 
		//logger.debug("forEachListener="+self.getForEachListener());

		this.root = self;
	}

	after(X self, JobExecutionContext jobExecutionContext): 
		execution(public void Job+.execute(..) throws JobExecutionException) 
		&& this(self) 
		&& args(jobExecutionContext) {

		//logger.debug("ASPECT AFTER:execution(public void Job+.execute(..) throws JobExecutionException) && this(self) && args(jobExecutionContext)");
		jobExecutionContext.setResult(result);
	}
	
	after(X self): 
		execution(protected KCall.new(..)) && this(self) {

		//logger.debug("ASPECT AFTER:protected execution(protected KCall.new(..)) && this(self)");
		//logger.debug("self="+self);
		self.setRestartedFireInstanceId(root.getRestartedFireInstanceId()); 
		self.setForEachListener(root.getForEachListener());
		//logger.debug("restartedFireInstanceId="+self.getRestartedFireInstanceId()); 
		//logger.debug("forEachListener="+self.getForEachListener());
	}

	after(X self): 
		execution(protected KProcess.new(..)) && this(self) {

		//logger.debug("ASPECT AFTER:protected execution(protected KProcess.new(..)) && this(self)");
		//logger.debug("self="+self);
		self.setRestartedFireInstanceId(root.getRestartedFireInstanceId()); 
		self.setForEachListener(root.getForEachListener());
		//logger.debug("restartedFireInstanceId="+self.getRestartedFireInstanceId()); 
		//logger.debug("forEachListener="+self.getForEachListener());
	}

    pointcut enter(KCall _kCall): call(* org.parallelj.internal.kernel.callback.Entry+.enter(KCall)) && args(_kCall);
    pointcut invoke(): call(public Object Method.invoke(Object, ..)) && !within(QuartzContextAdapter);

// restart part ----------------------------------------------------------------------------------------------------------------------------------------
    Object around(Object oo, KCall _kCall)  :
        invoke() && args(oo, ..) && cflow(enter(_kCall)) {
               
        if (restart(_kCall, oo)) {
               return proceed(oo, _kCall);
        } else {
               return null;
        }
     }

	private boolean restart(KCall _kCall, Object program) {
		boolean result = true;
		if (program != null) {
			if (program.getClass().isAnnotationPresent(TrackNRestart.class)) {
				String oid = getOID(program);
				if (oid != null) {
					String instanceId = ((X) _kCall)
							.getRestartedFireInstanceId();
					if (instanceId != null) {
						// logger.debug("Processing in restarting mode for oid : ["
						// + oid + "] ,program : [" + program +
						// "] ,instanceId :[" +instanceId+ "]");
						try {
							result = !((X) _kCall)
									.getForEachListener()
									.isForEachInstanceIgnorable(instanceId, oid);
							// logger.debug(result ?
							// "This OID has not been already processed or" +
							// " already processed in error, so its processing is not skipped":
							// "This OID ("+oid+") has been already successfully processed, so its processing is skipped");
						} catch (JobPersistenceException e) {
							abortAbruptly();
							throw new TrackNRestartException(
									"Unable to read status for iteration '"
											+ oid + "'.", e);
						} catch (SQLException e) {
							abortAbruptly();
							throw new TrackNRestartException(
									"Unable to read status for iteration '"
											+ oid + "'.", e);
						}
					} else {
						// logger.debug("Processing in normal mode (ie not restarting mode) for : ["
						// + oid + "] ,program : [" + program + "]");
					}
				}
			}
		}
		return result;
	}
    
	private String getOID(Object program) {
		String result = null;
		try {
			Method m = program.getClass().getDeclaredMethod("getOID",
					new Class[] {});
			return (String) m.invoke(program, new Object[] {});
		} catch (SecurityException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (NoSuchMethodException e1) {
			abortAbruptly();
			throw new TrackNRestartException(
					"No getOID() method found while program " + program
							+ " was annotated @TrackNRestart.", e1);
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

// tracking part ----------------------------------------------------------------------------------------------------------------------------------------
   after(Object oo, KCall _kCall) returning :
    	invoke() && args(oo, ..) && cflow(enter(_kCall)) {
		
		//logger.debug("ASPECT AFTER RETURNING");
		//logger.debug("_KCall="+_kCall);
		track(_kCall, oo, true, null);
    }

    after(Object oo, KCall _kCall) throwing (InvocationTargetException ite) : 
    	invoke() && args(oo, ..) && cflow(enter(_kCall)) {
		
		//logger.debug("ASPECT AFTER THROWING (InvocationTargetException)");
		//logger.debug("_KCall="+_kCall);
		this.result = "FAILURE";
		track(_kCall, oo, false, ite);
    }

    
	private void track(KCall _kCall, Object program, boolean success,
			InvocationTargetException ite) {
		String oid = null;

		try {
			if (program != null) {
				Class<? extends Object> clazz = program.getClass();
				if (clazz.isAnnotationPresent(TrackNRestart.class)) {
					Method m = clazz
							.getDeclaredMethod("getOID", new Class[] {});
					oid = (String) m.invoke(program, new Object[] {});
					if (oid != null) {
						if (success == true) {
							persist(_kCall, success, oid);
						} else {
							Class<? extends Throwable>[] filteredExceptions = clazz
									.getAnnotation(TrackNRestart.class)
									.filteredExceptions();
							if (isIteCausePermittedInList(ite,
									filteredExceptions)) {
								persist(_kCall, success, oid);
							} else {
								abortAbruptly();
								throw new TrackNRestartException(
										"Exception thrown ("
												+ ite.getCause().getClass()
														.getName()
												+ ") is not in list of permitted exceptions "
												+ filteredExceptionsAsString(filteredExceptions));
							}
						}
						if (logger.isDebugEnabled()) {
							// logger.debug("oid='" + oid +
							// "' persisted with status='" + (success ?
							// ReturnCodes.SUCCESS : "FAILURE") + "' "+ (ite ==
							// null ? "" : ", cause : " + ite.getCause()));
						}
					} else {
						abortAbruptly();
						throw new TrackNRestartException(
								"Unable to get iteration OID while 'tracking' context.");
					}
				}
			}
		} catch (NoSuchMethodException e) {
			abortAbruptly();
			throw new TrackNRestartException(
					"No getOID() method found while program " + program
							+ " was annotated @TrackNRestart.", e);
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JobPersistenceException e) {
			abortAbruptly();
			throw new TrackNRestartException("Unable to persist status ['"
					+ (success ? ReturnCodes.SUCCESS : "FAILURE")
					+ "'] for iteration '" + oid + "'.", e);
		} catch (SQLException e) {
			abortAbruptly();
			throw new TrackNRestartException("Unable to persist status ['"
					+ (success ? ReturnCodes.SUCCESS : "FAILURE")
					+ "'] for iteration '" + oid + "'.", e);
		}
	}

	private boolean isIteCausePermittedInList(InvocationTargetException ite,
			Class<? extends Throwable>[] filteredExceptions) {
		for (int i = 0; i < filteredExceptions.length; i++) {
			if (filteredExceptions[i].isAssignableFrom(ite.getCause()
					.getClass()))
				return true;
		}
		return false;
	}

	private String filteredExceptionsAsString(
			Class<? extends Throwable>[] filteredExceptions) {
		StringBuffer sb = new StringBuffer();
		sb.append("[");
		for (int i = 0; i < filteredExceptions.length; i++) {
			if (i > 0)
				sb.append(", ");
			sb.append(filteredExceptions[i].getName());
		}
		sb.append("]");
		return sb.toString();
	}

	private void persist(KCall _kCall, boolean success, String oid)
			throws JobPersistenceException, SQLException {
		((X) _kCall).getForEachListener().forEachInstanceComplete(oid, success);
	}

	private void abortAbruptly() {
		this.result = "ABORTED";
		Programs.as((Adapter) this.root).abort();
	}

}
