package org.parallelj.tracknrestart.aspects;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.List;

import org.apache.log4j.Logger;
import org.parallelj.Programs;
import org.parallelj.Programs.ProcessHelper;
import org.parallelj.internal.kernel.KCall;
import org.parallelj.internal.kernel.KReflection;
import org.parallelj.internal.reflect.ProgramAdapter.Adapter;
import org.parallelj.mirror.Procedure;
import org.parallelj.mirror.ProgramType;
import org.parallelj.tracknrestart.annotations.TrackNRestart;
import org.parallelj.tracknrestart.listeners.ForEachListener;
import org.parallelj.tracknrestart.plugins.TrackNRestartPlugin;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobPersistenceException;

import java.lang.reflect.Field;


privileged public aspect QuartzContextAdapter  {
	
	static Logger logger = Logger.getLogger("org.parallelj.tracknrestart.QuartzContextAdapter");
	
	private X root;
	
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
		(org.parallelj.mirror.Element+) implements X;      

	declare parents:   
		(org.parallelj.internal.kernel.callback.Entry+) implements X; 
	
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
	//JobExecutionContext --> KProgram
	//JobExecutionContext --> KProcedure
	before(X self, JobExecutionContext jobExecutionContext): 
		execution(public void Job+.execute(..) throws JobExecutionException) 
		&& this(self) 
		&& args(jobExecutionContext) {

		this.root = self;

		logger.debug("ASPECT BEFORE:execution(public void Job+.execute(..) throws JobExecutionException) && this(self) && args(jobExecutionContext)");
		self.setRestartedFireInstanceId(jobExecutionContext.getJobDetail().getJobDataMap().getString(TrackNRestartPlugin.RESTARTED_FIRE_INSTANCE_ID));
		self.setForEachListener((ForEachListener) jobExecutionContext.getJobDetail().getJobDataMap().get(TrackNRestartPlugin.FOR_EACH_LISTENER));
		logger.debug("job="+self); 
		logger.debug("restartedFireInstanceId="+self.getRestartedFireInstanceId()); 
		logger.debug("forEachListener="+self.getForEachListener());
		
		for (ProgramType programType : (List<ProgramType>)KReflection.getInstance().getPrograms()) { 
			logger.debug("..program="+programType);
			((X)programType).setRestartedFireInstanceId(self.getRestartedFireInstanceId()); 
			((X)programType).setForEachListener(self.getForEachListener());
			logger.debug("..restartedFireInstanceId="+((X)programType).getRestartedFireInstanceId()); 
			logger.debug("..forEachListener="+((X)programType).getForEachListener());
			for (Procedure procedure : programType.getProcedures()) { 
				logger.debug("....procedure="+procedure);
				((X)procedure).setRestartedFireInstanceId(self.getRestartedFireInstanceId()); 
				((X)procedure).setForEachListener(self.getForEachListener());
				logger.debug("....restartedFireInstanceId="+((X)procedure).getRestartedFireInstanceId()); 
				logger.debug("....forEachListener="+((X)procedure).getForEachListener());
			}
		}
	}


	before(X caller, X callee): 
		call(* org.parallelj.internal.kernel.callback.Entry+.enter(..))
		&& target(callee) && this(caller){

		logger.debug("ASPECT BEFORE:call(* org.parallelj.internal.kernel.callback.Entry+.enter(..)) && target(callee) && this(caller)");
		logger.debug("caller="+caller);
		logger.debug("callee="+callee);
		callee.setRestartedFireInstanceId(caller.getRestartedFireInstanceId()); 
		callee.setForEachListener(caller.getForEachListener());
		logger.debug("restartedFireInstanceId="+callee.getRestartedFireInstanceId()); 
		logger.debug("forEachListener="+callee.getForEachListener());
	}

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

	after(X self, Object oo) returning : 
		withincode(* org.parallelj.internal.kernel.callback.Entry+.enter(KCall)) 
		&& call(public Object Method.invoke(Object, ..)) 
		&& this(self) && args(oo, ..){
		
		logger.debug("ASPECT AFTER RETURNING:withincode(* org.parallelj.internal.kernel.callback.Entry+.enter(KCall)) && call(public Object Method.invoke(Object, ..)) && this(self) && args(oo, ..)");
		track(self, oo, true, null);
	}

	after(X self, Object oo) throwing (InvocationTargetException ite) : 
		withincode(* org.parallelj.internal.kernel.callback.Entry+.enter(KCall)) 
		&& call(public Object Method.invoke(Object, ..)) 
		&& this(self) && args(oo, ..){
		
		logger.debug("ASPECT AFTER THROWING (InvocationTargetException):withincode(* org.parallelj.internal.kernel.callback.Entry+.enter(KCall)) && call(public Object Method.invoke(Object, ..)) && this(self) && args(oo, ..)");
		track(self, oo, false, ite);
	}

	private void track(X self, Object program, boolean success, InvocationTargetException ite) {
		String oid = null;
		try {
			if (program != null) {
				if (program.getClass().isAnnotationPresent(TrackNRestart.class)){
					Method m = program.getClass().getDeclaredMethod("getOID", new Class[]{});
					oid = (String) m.invoke(program, new Object[] {});
					if (oid != null) {
						self.getForEachListener().forEachInstanceComplete(oid, success);
						if (logger.isDebugEnabled()) {
							logger.info("oid='" + oid + "' persisted with status='" + (success?"SUCCESS":"FAILURE") + "' "+(ite==null?"":", cause : "+ite.getCause()));
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			abortAbruptly();
			throw new TrackNRestartException("Unable to persist status ['" + (success?"SUCCESS":"FAILURE") + "'] for iteration '" + oid + "'.",e);
		}
	}

	private void abortAbruptly() {
//		this.jobExecutionContext.setResult("FAILURE");
		Programs.as((Adapter) this.root).abort();
	}

}
