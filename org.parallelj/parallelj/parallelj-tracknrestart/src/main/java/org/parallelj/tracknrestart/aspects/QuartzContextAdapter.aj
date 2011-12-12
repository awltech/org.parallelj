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

privileged public aspect QuartzContextAdapter  {
	
	declare precedence :
		org.parallelj.tracknrestart.aspects.QuartzContextAdapter,
		org.parallelj.launching.quartz.JobsAdapter;

	static Logger logger = Logger.getLogger("org.parallelj.tracknrestart.QuartzContextAdapter");
	
	private X root;

	String result = "SUCCESS";
	
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

		logger.debug("ASPECT BEFORE:execution(public void Job+.execute(..) throws JobExecutionException) && this(self) && args(jobExecutionContext)");

		self.setRestartedFireInstanceId(jobExecutionContext.getJobDetail().getJobDataMap().getString(TrackNRestartPlugin.RESTARTED_FIRE_INSTANCE_ID));
		self.setForEachListener((ForEachListener) jobExecutionContext.getJobDetail().getJobDataMap().get(TrackNRestartPlugin.FOR_EACH_LISTENER));
		logger.debug("job="+self); 
		logger.debug("restartedFireInstanceId="+self.getRestartedFireInstanceId()); 
		logger.debug("forEachListener="+self.getForEachListener());

		this.root = self;

//		for (ProgramType programType : (List<ProgramType>)KReflection.getInstance().getPrograms()) { 
//			logger.debug("..program="+programType);
//			((X)programType).setRestartedFireInstanceId(self.getRestartedFireInstanceId()); 
//			((X)programType).setForEachListener(self.getForEachListener());
//			logger.debug("..restartedFireInstanceId="+((X)programType).getRestartedFireInstanceId()); 
//			logger.debug("..forEachListener="+((X)programType).getForEachListener());
//			for (Procedure procedure : programType.getProcedures()) { 
//				logger.debug("....procedure="+procedure);
//				((X)procedure).setRestartedFireInstanceId(self.getRestartedFireInstanceId()); 
//				((X)procedure).setForEachListener(self.getForEachListener());
//				logger.debug("....restartedFireInstanceId="+((X)procedure).getRestartedFireInstanceId()); 
//				logger.debug("....forEachListener="+((X)procedure).getForEachListener());
//			}
//		}
	}

	after(X self, JobExecutionContext jobExecutionContext): 
		execution(public void Job+.execute(..) throws JobExecutionException) 
		&& this(self) 
		&& args(jobExecutionContext) {

		logger.debug("ASPECT AFTER:execution(public void Job+.execute(..) throws JobExecutionException) && this(self) && args(jobExecutionContext)");
		jobExecutionContext.setResult(result);
	}
	
	after(X self): 
		execution(protected KCall.new(..)) && this(self) {

		logger.debug("ASPECT AFTER:protected execution(protected KCall.new(..)) && this(self)");
		logger.debug("self="+self);
		self.setRestartedFireInstanceId(root.getRestartedFireInstanceId()); 
		self.setForEachListener(root.getForEachListener());
		logger.debug("restartedFireInstanceId="+self.getRestartedFireInstanceId()); 
		logger.debug("forEachListener="+self.getForEachListener());
	}

	after(X self): 
		execution(protected KProcess.new(..)) && this(self) {

		logger.debug("ASPECT AFTER:protected execution(protected KProcess.new(..)) && this(self)");
		logger.debug("self="+self);
		self.setRestartedFireInstanceId(root.getRestartedFireInstanceId()); 
		self.setForEachListener(root.getForEachListener());
		logger.debug("restartedFireInstanceId="+self.getRestartedFireInstanceId()); 
		logger.debug("forEachListener="+self.getForEachListener());
	}

//	before(X caller, X callee): 
//		call(* org.parallelj.internal.kernel.callback.Entry+.enter(..))
//		&& target(callee) && this(caller){
//
//		logger.debug("ASPECT BEFORE:call(* org.parallelj.internal.kernel.callback.Entry+.enter(..)) && target(callee) && this(caller)");
//		logger.debug("caller="+caller);
//		logger.debug("callee="+callee);
//		callee.setRestartedFireInstanceId(caller.getRestartedFireInstanceId()); 
//		callee.setForEachListener(caller.getForEachListener());
//		logger.debug("restartedFireInstanceId="+callee.getRestartedFireInstanceId()); 
//		logger.debug("forEachListener="+callee.getForEachListener());
//	}

//	before(X caller, X callee): 
//		call(* org.parallelj.internal.kernel.callback.Exit+.exit(..))
//		&& target(callee) && this(caller){
//
//		logger.debug("ASPECT BEFORE:call(* org.parallelj.internal.kernel.callback.Exit+.exit(..)) && target(callee) && this(caller)");
//		logger.debug("caller="+caller);
//		logger.debug("callee="+callee);
//		callee.setRestartedFireInstanceId(caller.getRestartedFireInstanceId()); 
//		callee.setForEachListener(caller.getForEachListener());
//		logger.debug("restartedFireInstanceId="+callee.getRestartedFireInstanceId()); 
//		logger.debug("forEachListener="+callee.getForEachListener());
//	}

//	void around(X self, KCall _call): 
//		execution(* org.parallelj.internal.kernel.callback.Entry+.enter(..))
//		&& this(self) && args(_call){
//
//		try {
//			if (self.getRestartedFireInstanceId() != null) {
//				Object c = _call.getProcess().getContext();
//				if (c != null) {
//					Method m = c.getClass().getDeclaredMethod("getOID", new Class[]{});
//					String oid = (String) m.invoke(_call.getProcess().getContext(),	new Object[] {});
//					if (oid != null) {
//						boolean wasSuccess = self.getForEachListener().isForEachInstanceIgnorable(self.getRestartedFireInstanceId(), oid);
//						if (wasSuccess) {
//							// ?
//						} else {
//							proceed(self, _call);
//						}
//					} else {
//						throw new TrackNRestartException("Unable to get iteration OID while 'restarting' context.");
//					}
//				}
//			} else {
//				proceed(self, _call);
//			}
//		} catch (NoSuchMethodException e) {
//			// ignore
//		} catch (SecurityException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IllegalArgumentException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IllegalAccessException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (InvocationTargetException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (TrackNRestartException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}

    pointcut enter(KCall _kCall): call(* org.parallelj.internal.kernel.callback.Entry+.enter(KCall)) && args(_kCall);
    pointcut invoke(): call(public Object Method.invoke(Object, ..)) && !within(QuartzContextAdapter);
    
    after(Object oo, KCall _kCall) returning :
    	invoke() && args(oo, ..) && cflow(enter(_kCall)) {
		
		logger.debug("ASPECT AFTER RETURNING");
		logger.debug("_KCall="+_kCall);
		track(_kCall, oo, true, null);
    }

    after(Object oo, KCall _kCall) throwing (InvocationTargetException ite) : 
    	invoke() && args(oo, ..) && cflow(enter(_kCall)) {
		
		logger.debug("ASPECT AFTER THROWING (InvocationTargetException)");
		logger.debug("_KCall="+_kCall);
		this.result = "PARTIAL";
		track(_kCall, oo, false, ite);
    }

    
    private void track(KCall _kCall, Object program, boolean success, InvocationTargetException ite) {
		String oid = null;
		
		try {
			if (program != null) {
				Class<? extends Object> clazz = program.getClass();
				if (clazz.isAnnotationPresent(TrackNRestart.class)){
					Method m = clazz.getDeclaredMethod("getOID", new Class[]{});
					oid = (String) m.invoke(program, new Object[] {});
					if (oid != null) {
						if (success == true) {
							persist(_kCall, success, oid);
						} else {
							Class<? extends Throwable>[] filteredExceptions = clazz.getAnnotation(TrackNRestart.class).filteredExceptions();
							if (isIteCausePermittedInList(ite, filteredExceptions)) {
								persist(_kCall, success, oid);
							} else {
								abortAbruptly();
								throw new TrackNRestartException("Exception thrown ("+ite.getCause().getClass().getName()+") is not in list of permitted exceptions "+filteredExceptionsAsString(filteredExceptions));
							}
						}
						if (logger.isDebugEnabled()) {
							logger.info("oid='"	+ oid + "' persisted with status='"	+ (success ? "SUCCESS" : "FAILURE")	+ "' "+ (ite == null ? "" : ", cause : " + ite.getCause()));
						}
					} else {
						abortAbruptly();
						throw new TrackNRestartException("Unable to get iteration OID while 'tracking' context.");
					}
				}
			}
		} catch (NoSuchMethodException e) {
			abortAbruptly();
			throw new TrackNRestartException("No getOID() method found while program "+program+" was annotated @TrackNRestart.",e);
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
			throw new TrackNRestartException("Unable to persist status ['" + (success?"SUCCESS":"FAILURE") + "'] for iteration '" + oid + "'.",e);
		} catch (SQLException e) {
			abortAbruptly();
			throw new TrackNRestartException("Unable to persist status ['" + (success?"SUCCESS":"FAILURE") + "'] for iteration '" + oid + "'.",e);
		}
	}

	private boolean isIteCausePermittedInList(InvocationTargetException ite, Class<? extends Throwable>[] filteredExceptions) {
		for (int i = 0; i < filteredExceptions.length; i++) {
			if (filteredExceptions[i].isAssignableFrom(ite.getCause().getClass()))
				return true;
		}
		return false;
	}
	
	private String filteredExceptionsAsString(Class<? extends Throwable>[] filteredExceptions) {
		StringBuffer sb = new StringBuffer();
		sb.append("[");
		for (int i = 0; i < filteredExceptions.length; i++) {
			if (i>0) 
				sb.append(", ");
			sb.append(filteredExceptions[i].getName());
		}
		sb.append("]");
		return sb.toString();
	}

	private void persist(KCall _kCall, boolean success, String oid) throws JobPersistenceException, SQLException {
		((X) _kCall).getForEachListener().forEachInstanceComplete(oid, success);
	}

	private void abortAbruptly() {
		this.result = "FAILURE";
		Programs.as((Adapter) this.root).abort();
	}

}
