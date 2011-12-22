package org.parallelj.tracknrestart.aspects;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.parallelj.Programs;
import org.parallelj.Programs.ProcessHelper;
import org.parallelj.internal.kernel.KCall;
import org.parallelj.internal.kernel.KProcess;
import org.parallelj.internal.reflect.ProgramAdapter.Adapter;
import org.parallelj.tracknrestart.ReturnCodes;
import org.parallelj.tracknrestart.annotations.TrackNRestart;
import org.parallelj.tracknrestart.databinding.ProgramFieldsBinder;
import org.parallelj.tracknrestart.listeners.ForEachListener;
import org.parallelj.tracknrestart.plugins.TrackNRestartPluginAll;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobPersistenceException;

privileged public aspect QuartzContextAdapter percflow (execution(public void Job+.execute(..) throws JobExecutionException)) {
	
	public static final String RETURN_CODE = "RETURN_CODE";

	declare precedence :
		org.parallelj.tracknrestart.aspects.QuartzContextAdapter,
		org.parallelj.launching.quartz.JobsAdapter;

	static Logger logger = Logger.getLogger("org.parallelj.tracknrestart");
	
	private X root;

	String result = ReturnCodes.SUCCESS.name();
	
//	Throwable exceptionThrown = null;
	
	//---------------------------------------------------------------------------------------------------
	
	declare parents:
		(@org.parallelj.launching.QuartzExecution *) implements Job;

	public void Job.execute(JobExecutionContext context) throws JobExecutionException {
		
		try {
			logger.debug("-----------------------------------------------------------------------------------------------------");
			logger.debug("STARTING //J Root Program "+this.getClass().getName());
	
			ProcessHelper<?> p = null;
//			context.setResult(new String());
//			context.setResult(new HashMap<String, Serializable>());
			context.setResult(new JobDataMap());
//			ProgramFieldsBinder.setProgramInputFields(this, context);
			p = Programs.as((Adapter) this).execute().join();
			ProgramFieldsBinder.getProgramOutputFields(this, context);

			X xx = (X)this;
			if (xx.exceptionThrown != null){
				throw new JobExecutionException(xx.exceptionThrown);
			}
		} catch (IllegalAccessException e) {
			throw new JobExecutionException(e);
		} catch (NoSuchFieldException e) {
			throw new JobExecutionException(e);
		} finally {
			logger.debug("ENDING   //J Root Program "+this.getClass().getName());
			logger.debug("-----------------------------------------------------------------------------------------------------");
		}
//		context.setResult(String.valueOf(p.getState()));
	}

	//---------------------------------------------------------------------------------------------------
	
	public interface X {};

	declare parents:           
		(org.quartz.Job+ && !org.quartz.Job) implements X;    

	declare parents:   
		(org.parallelj.internal.kernel.KCall) implements X;      

	private Throwable X.exceptionThrown = null;

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

		self.setRestartedFireInstanceId(jobExecutionContext.getJobDetail().getJobDataMap().getString(TrackNRestartPluginAll.RESTARTED_FIRE_INSTANCE_ID));
		self.setForEachListener((ForEachListener) jobExecutionContext.getJobDetail().getJobDataMap().get(TrackNRestartPluginAll.FOR_EACH_LISTENER));
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

		Object oResult = jobExecutionContext.getResult();
		if (oResult instanceof JobDataMap){
			JobDataMap resultAsJobDataMap = (JobDataMap) oResult;
			resultAsJobDataMap.put(RETURN_CODE, this.result);
		} else {
			jobExecutionContext.setResult(this.result);
		}
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

// Runnable program. ----------------------------------------------------------------------------------------------------------------------------------

    public interface Y {};
    
  	private Object Y.context = null;
  	
  	public Object Y.getContext() {
  		return context;
  	}
  	
  	public void Y.setContext(Object context) {
  		this.context=context;
  	}
    
    declare parents:           
		(java.lang.Runnable+ ) implements X, Y;    
    
    // 1) Transfert from RunnableCall to Runnable. 
  	after(Object self) returning (Runnable ret): 
    	 execution(* org.parallelj.internal.kernel.procedure.RunnableProcedure.RunnableCall.toRunnable())
      && this(self)  {
    	
    	 ((X)ret).setForEachListener(((X)self).getForEachListener());
    	 ((X)ret).setRestartedFireInstanceId(((X)self).getRestartedFireInstanceId());
    	 ((Y)ret).setContext(((org.parallelj.internal.kernel.KCall)self).getContext());
    }
    
     // 2) Interception around call of run.      
     void around(Object self): call(* run(..))
     && within(org.parallelj.internal.kernel.procedure.RunnableProcedure.RunnableCall)
     && within(Runnable+)  && this(self)  {
    	 
    	 String restartedFireInstanceId = ((X) self).getRestartedFireInstanceId();
    	 ForEachListener forEachListener = ((X)self).getForEachListener();	
    	 Object program =  ((Y)self).getContext();
    	 
    	 if (isToProcess(restartedFireInstanceId,forEachListener,program)) {
    		try { 
    			proceed(self);
     			if (isTrackNRestartAnnoted(program)) {
     				String oid = getOID(program);
   					track(forEachListener, oid, true);
     			}
    		} catch (Exception e) {
 				if (isTrackNRestartAnnoted(program)){
 					String oid = getOID(program);
 					if (isTrackNRestartExceptionPermitted(program, e)) {
 						this.result = ReturnCodes.FAILURE.name();
	 					track(forEachListener, oid, false);
					} else {
						abortAbruptly(new TrackNRestartException("Exception thrown ("+e.getClass().getName()+") is not in list of permitted exceptions "+filteredExceptionsAsString(getTrackNRestartAnnotedException(program))));
					}
 				}
    		}
     	} else {
     		if (isTrackNRestartAnnoted(program)) {
 				String oid = getOID(program);
 				track(forEachListener, oid, true);
			} else {
				abortAbruptly(new TrackNRestartException("Unable to get iteration OID while 'tracking' context."));
 			}
     	}
     }
     
     /**
      * Track the execution (success or failure).
      * @param forEachListener listener handling Quartz backend.
      * @param oid unique identifier
      * @param success boolean indicating if processing is a success or a failure.
      */
     private void track(ForEachListener forEachListener, String oid, boolean success) {
	     try {
				forEachListener.forEachInstanceComplete(oid, success);
	     } catch (JobPersistenceException e) {
				abortAbruptly(new TrackNRestartException(e));
//				throw new TrackNRestartException("Unable to read status for iteration '"+ oid + "'.", e);
		} catch (SQLException e) {
				abortAbruptly(new TrackNRestartException(e));
//				throw new TrackNRestartException("Unable to read status for iteration '"+ oid + "'.", e);
		}
     }
    
 
// restart part ----------------------------------------------------------------------------------------------------------------------------------------
     
     Object around(Object oo, KCall _kCall)  :
        invoke() && args(oo, ..) && cflow(enter(_kCall)) {
         
    	if (isToProcess( ((X) _kCall).getRestartedFireInstanceId(),((X) _kCall).getForEachListener(), oo)) {
               return proceed(oo, _kCall);
        } else {
               return null;
        }
     }

    /**
     * Check if an iterable must be processed. This means if the iterable is not already processed or the element was processed in error.
     * @param restartedFireInstanceId id of the batch to be restarted.
     * @param forEachListener listener handling request to Quartz. 
     * @param program instance of the program processing the iterable. 
     * @return true iterable has to be processed, false else.
     */
    private boolean isToProcess(String restartedFireInstanceId,  ForEachListener forEachListener, Object program)   {
		boolean result = true;
		if (program != null) {
			if (program.getClass().isAnnotationPresent(TrackNRestart.class)) {
				String oid = getOID(program);
				if (oid != null) {
					if (restartedFireInstanceId != null) {
						// logger.debug("Processing in restarting mode for oid : ["
						// + oid + "] ,program : [" + program +
						// "] ,instanceId :[" +instanceId+ "]");
						try {
							result = !forEachListener.isForEachInstanceIgnorable(restartedFireInstanceId, oid);
							// logger.debug(result ?
							// "This OID has not been already processed or" +
							// " already processed in error, so its processing is not skipped":
							// "This OID ("+oid+") has been already successfully processed, so its processing is skipped");
						} catch (JobPersistenceException e) {
							abortAbruptly(new TrackNRestartException(e));
//							throw new TrackNRestartException(
//									"Unable to read status for iteration '"
//											+ oid + "'.", e);
						} catch (SQLException e) {
							abortAbruptly(new TrackNRestartException(e));
//							throw new TrackNRestartException(
//									"Unable to read status for iteration '"
//											+ oid + "'.", e);
						}
					} else {
						// logger.debug("Processing in normal mode (ie not restarting mode) for : ["
						// + oid + "] ,program : [" + program + "]");
					}
				} else {
					abortAbruptly(new TrackNRestartException("Unable to get iteration OID while 'restarting' context."));
				}
			}
		}
		return result;
	}
    
    /**
     * Get unique identifier returned by the program (the program contains value to be processed).
     * @param program
     * @return unique identifier.
     */
	private String getOID(Object program) {
		String result = null;
		try {
			Method m = program.getClass().getDeclaredMethod("getOID", new Class[] {});
			return (String) m.invoke(program, new Object[] {});
		} catch (SecurityException e) {
			abortAbruptly(new TrackNRestartException(e));
		} catch (NoSuchMethodException e) {
			abortAbruptly(e);
//			throw new TrackNRestartException(
//					"No getOID() method found while program " + program
//							+ " was annotated @TrackNRestart.", e1);
		} catch (IllegalArgumentException e) {
			abortAbruptly(new TrackNRestartException(e));
		} catch (IllegalAccessException e) {
			abortAbruptly(new TrackNRestartException(e));
		} catch (InvocationTargetException e) {
			abortAbruptly(new TrackNRestartException(e));
		}
		
		return result;
	}
	
	/**
	 * Indicate of the Program contains annotation TrackNRestart.
	 * @param program
	 * @return true if program is annoted.
	 */
	private boolean isTrackNRestartAnnoted(Object program) {
		return program.getClass().isAnnotationPresent(TrackNRestart.class);
	}

	/**
	 * Check if the raised exception belong to the allowed ones in TrackNRestart annotation.
	 * @param program
	 * @param exception raised
	 * @return true if exception is allowed, false else.
	 */
	private boolean isTrackNRestartExceptionPermitted(Object program, Exception exception) {
		boolean result = false;
		Class<? extends Throwable>[] filteredExceptions =  getTrackNRestartAnnotedException(program);
		for (int i = 0; i < filteredExceptions.length; i++) {
				if (filteredExceptions[i].isAssignableFrom(exception.getClass())) {
					return true;
			}
		}
		return false;
	}
	
	/**
	 * Return exceptions defined in TrackNRestart annotation.
	 * @param program
	 * @return array of exceptions
	 */
	private Class<? extends Throwable>[] getTrackNRestartAnnotedException (Object program) {
		return program.getClass().getAnnotation(TrackNRestart.class).filteredExceptions();
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
		this.result = ReturnCodes.FAILURE.name();
		track(_kCall, oo, false, ite);
    }

    
	private void track(KCall _kCall, Object program, boolean success,
			InvocationTargetException ite) {
		String oid = null;

		try {
			if (program != null) {
				Class<? extends Object> clazz = program.getClass();
				if (clazz.isAnnotationPresent(TrackNRestart.class)) {
					oid = getOID(program);
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
								abortAbruptly(
										new TrackNRestartException(
												"Exception thrown ("
												+ ite.getCause().getClass().getName()
												+ ") is not in list of permitted exceptions "
												+ filteredExceptionsAsString(filteredExceptions)));
							}
						}
						//if (logger.isDebugEnabled()) {
							// logger.debug("oid='" + oid +
							// "' persisted with status='" + (success ?
							// ReturnCodes.SUCCESS : ReturnCodes.FAILURE) + "' "+ (ite ==
							// null ? "" : ", cause : " + ite.getCause()));
						//}
					} else {
						abortAbruptly(new TrackNRestartException("Unable to get iteration OID while 'tracking' context."));
					}
				}
			}
		} catch (JobPersistenceException e) {
			abortAbruptly(new TrackNRestartException(e));
//			throw new TrackNRestartException("Unable to persist status ['"
//					+ (success ? ReturnCodes.SUCCESS : ReturnCodes.FAILURE)
//					+ "'] for iteration '" + oid + "'.", e);
		} catch (SQLException e) {
			abortAbruptly(new TrackNRestartException(e));
//			throw new TrackNRestartException("Unable to persist status ['"
//					+ (success ? ReturnCodes.SUCCESS : ReturnCodes.FAILURE)
//					+ "'] for iteration '" + oid + "'.", e);
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

	private void abortAbruptly(Throwable t) {
		this.result = "ABORTED";
		Programs.as((Adapter) this.root).abort();
		this.root.exceptionThrown = t;
	}

}
