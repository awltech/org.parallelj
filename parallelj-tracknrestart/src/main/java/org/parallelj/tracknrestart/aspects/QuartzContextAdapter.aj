/*
 *     ParallelJ, framework for parallel computing
 *
 *     Copyright (C) 2010, 2011, 2012 Atos Worldline or third-party contributors as
 *     indicated by the @author tags or express copyright attribution
 *     statements applied by the authors.
 *
 *     This library is free software; you can redistribute it and/or
 *     modify it under the terms of the GNU Lesser General Public
 *     License as published by the Free Software Foundation; either
 *     version 2.1 of the License.
 *
 *     This library is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 *     Lesser General Public License for more details.
 *
 *     You should have received a copy of the GNU Lesser General Public
 *     License along with this library; if not, write to the Free Software
 *     Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA
 */
package org.parallelj.tracknrestart.aspects;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.SQLException;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;

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
import org.parallelj.internal.kernel.procedure.RunnableProcedure;
import org.parallelj.internal.kernel.procedure.CallableProcedure;
import org.parallelj.launching.internal.LaunchingObservable;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobPersistenceException;

privileged public aspect QuartzContextAdapter percflow(execution(public void Job+.execute(..) throws JobExecutionException)) {
	
	declare precedence :
		org.parallelj.tracknrestart.aspects.QuartzContextAdapter,
		org.parallelj.launching.quartz.JobsAdapter;

	//---------------------------------------------------------------------------------------------------

	public static final String RETURN_CODE = "RETURN_CODE";

	static Logger logger = Logger.getLogger("org.parallelj.tracknrestart");
	
	private X root;

	String result = ReturnCodes.SUCCESS.name();
	
	//---------------------------------------------------------------------------------------------------
	
	declare parents:
		(@org.parallelj.launching.QuartzExecution *) implements Job;

	declare parents:           
		(org.quartz.Job+ && !org.quartz.Job) implements X;    

	declare parents:   
		(org.parallelj.internal.kernel.KCall) implements X;      

	//---------------------------------------------------------------------------------------------------
	
	public interface X {};
	
	private Throwable X.exceptionThrown = null;
	
	private ForEachListener X.forEachListener = null;       
	
	private String X.restartedFireInstanceId = null;       
	
	//---------------------------------------------------------------------------------------------------
	
	@SuppressWarnings("deprecation")
	public void Job.execute(JobExecutionContext context) throws JobExecutionException {
		
//		ExecutorService service = null;
		
		try {
			logger.debug("-----------------------------------------------------------------------------------------------------");
			logger.debug("STARTING //J Root Program "+this.getClass().getName());
	
			context.setResult(new JobDataMap());

//			service = Executors.newFixedThreadPool(5);

			ProcessHelper<?> processHelper = Programs.as((Adapter) this);
			//((JobDataMap)context.getResult()).put(QuartzUtils.CONTEXT, processHelper);
			
			LaunchingObservable observable = new LaunchingObservable();
			observable.prepareLaunching((Adapter) this, processHelper, context);
			
			processHelper.execute().join();
			//((JobDataMap)context.getResult()).put(QuartzUtils.CONTEXT, processHelper);
			
			observable.finalizeLaunching((Adapter) this, processHelper, context);
			ProgramFieldsBinder.getProgramOutputFields(this, context);

			X current = (X)this;
			if (current.exceptionThrown != null){
				throw new JobExecutionException(current.exceptionThrown);
			}
		} catch (IllegalAccessException e) {
			throw new JobExecutionException(e);
		} catch (NoSuchFieldException e) {
			throw new JobExecutionException(e);
		} finally {
			logger.debug("ENDING   //J Root Program "+this.getClass().getName());
			logger.debug("-----------------------------------------------------------------------------------------------------");
//			service.shutdown();
		}
	}

	//---------------------------------------------------------------------------------------------------
	
	//JobExecutionContext --> Job
	before(X self, JobExecutionContext jobExecutionContext): 
		execution(public void Job+.execute(..) throws JobExecutionException) 
		&& this(self) 
		&& args(jobExecutionContext) {

		self.restartedFireInstanceId = jobExecutionContext.getJobDetail().getJobDataMap().getString(TrackNRestartPluginAll.RESTARTED_FIRE_INSTANCE_ID);
		self.forEachListener = (ForEachListener)jobExecutionContext.getJobDetail().getJobDataMap().get(TrackNRestartPluginAll.FOR_EACH_LISTENER);

		this.root = self;
	}

	after(X self, JobExecutionContext jobExecutionContext): 
		execution(public void Job+.execute(..) throws JobExecutionException) 
		&& this(self) 
		&& args(jobExecutionContext) {

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

		self.restartedFireInstanceId = root.restartedFireInstanceId; 
		self.forEachListener = root.forEachListener;
	}

	after(X self): 
		execution(protected KProcess.new(..)) && this(self) {

		self.restartedFireInstanceId = root.restartedFireInstanceId; 
		self.forEachListener = root.forEachListener;
	}

	//---------------------------------------------------------------------------------------------------
	
    pointcut enter(KCall _kCall): call(* org.parallelj.internal.kernel.callback.Entry+.enter(KCall)) && args(_kCall);
    pointcut invoke(): call(public Object Method.invoke(Object, ..)) && !within(QuartzContextAdapter);

    // Runnable program. ----------------------------------------------------------------------------------------------------------------------------------

    declare parents:           
    	(java.lang.Runnable+ ) implements X, Y;    
    
	//---------------------------------------------------------------------------------------------------
	
    public interface Y {};
    
  	private Object Y.context = null;
  	
	//---------------------------------------------------------------------------------------------------
	
    // 1.1) Transfert from RunnableCall to Runnable. 
  	after(Object self) returning (Runnable ret): 
    	 execution(* org.parallelj.internal.kernel.procedure.RunnableProcedure.RunnableCall.toRunnable())
      && this(self)  {
    	
   	 ((X)ret).forEachListener = ((X)self).forEachListener;
   	 ((X)ret).restartedFireInstanceId = ((X)self).restartedFireInstanceId;
   	 ((Y)ret).context = ((org.parallelj.internal.kernel.KCall)self).context;
    }
    
    // 2.1) Transfert from CallableCall to Runnable. 
  	after(Object self) returning (Runnable ret): 
    	 execution(* org.parallelj.internal.kernel.procedure.CallableProcedure.CallableCall.toRunnable())
      && this(self)  {
    	
   	 ((X)ret).forEachListener = ((X)self).forEachListener;
   	 ((X)ret).restartedFireInstanceId = ((X)self).restartedFireInstanceId;
   	 ((Y)ret).context = ((org.parallelj.internal.kernel.KCall)self).context;
    }
  	
	public void skip(org.parallelj.internal.kernel.KCall kCall) {
		kCall.start();
		kCall.complete();
	}

    
    // 1.2) Interception around call of run (from RunnableCall).      
	void around(org.parallelj.internal.kernel.KProcessor.KProcessorRunnable self):
		call(public void run())
		&& target(org.parallelj.internal.kernel.procedure.RunnableProcedure.RunnableCall.RunnableCallRunnable)
		&& this(self) {
   	 
   	 Runnable runnable = ((org.parallelj.internal.kernel.KProcessor.KProcessorRunnable)self).getRunnable();
	 String restartedFireInstanceId = ((X)runnable).restartedFireInstanceId;
	 ForEachListener forEachListener = ((X)runnable).forEachListener;
   	 Object program =  ((Y)runnable).context;
   	 if (isToProcess(restartedFireInstanceId,forEachListener,program)) {
   			proceed(self);
   			Exception exec = ((RunnableProcedure.RunnableCall)((RunnableProcedure.RunnableCall.RunnableCallRunnable)thisJoinPoint.getTarget()).getRunnableCall()).getException();
   			if (exec == null) {
       			if (isTrackNRestartAnnoted(program)) {
    				String oid = getOID(program);
  					track(forEachListener, oid, true);
    			}
   			} else {
				if (isTrackNRestartAnnoted(program)){
					String oid = getOID(program);
					if (isTrackNRestartExceptionPermitted(program, exec)) {
						this.result = ReturnCodes.FAILURE.name();
	 					track(forEachListener, oid, false);
					} else {
						abortAbruptly(new TrackNRestartException("Exception thrown ("+exec.getClass().getName()+") is not in list of permitted exceptions "+filteredExceptionsAsString(getTrackNRestartAnnotedException(program))));
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
    		// Inform the engine that this Runnable has been called
    		skip(((RunnableProcedure.RunnableCall)((RunnableProcedure.RunnableCall.RunnableCallRunnable)thisJoinPoint.getTarget()).getRunnableCall()));
    	}
    }
    
    // 2.2) Interception around call of run (from CallableCall).      
	void around(org.parallelj.internal.kernel.KProcessor.KProcessorRunnable self):
		call(public void run())
		&& target(org.parallelj.internal.kernel.procedure.CallableProcedure.CallableCall.CallableCallRunnable)
		&& this(self) {
   	 
   	 Runnable runnable = ((org.parallelj.internal.kernel.KProcessor.KProcessorRunnable)self).getRunnable();
	 String restartedFireInstanceId = ((X)runnable).restartedFireInstanceId;
	 ForEachListener forEachListener = ((X)runnable).forEachListener;
   	 Object program =  ((Y)runnable).context;
   	 if (isToProcess(restartedFireInstanceId,forEachListener,program)) {
   			proceed(self);
   			Exception exec = ((CallableProcedure.CallableCall)((CallableProcedure.CallableCall.CallableCallRunnable)thisJoinPoint.getTarget()).getCallableCall()).getException();
   			if (exec == null) {
       			if (isTrackNRestartAnnoted(program)) {
    				String oid = getOID(program);
  					track(forEachListener, oid, true);
    			}
   			} else {
				if (isTrackNRestartAnnoted(program)){
					String oid = getOID(program);
					if (isTrackNRestartExceptionPermitted(program, exec)) {
						this.result = ReturnCodes.FAILURE.name();
	 					track(forEachListener, oid, false);
					} else {
						abortAbruptly(new TrackNRestartException("Exception thrown ("+exec.getClass().getName()+") is not in list of permitted exceptions "+filteredExceptionsAsString(getTrackNRestartAnnotedException(program))));
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
    		// Inform the engine that this Runnable has been called
			skip(((CallableProcedure.CallableCall)((CallableProcedure.CallableCall.CallableCallRunnable)thisJoinPoint.getTarget()).getCallableCall()));
		}
    }
    
 	//---------------------------------------------------------------------------------------------------
 	
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
				abortAbruptly(new TrackNRestartException("Unable to read status for iteration '"+ oid + "'.", e));
		} catch (SQLException e) {
				abortAbruptly(new TrackNRestartException("Unable to read status for iteration '"+ oid + "'.", e));
		}
     }
    
 
     // restart part ----------------------------------------------------------------------------------------------------------------------------------------
     
     Object around(Object oo, KCall _kCall)  :
        invoke() && args(oo, ..) && cflow(enter(_kCall)) {
         
       	if (isToProcess(((X)_kCall).restartedFireInstanceId, ((X)_kCall).forEachListener, oo)) {
               return proceed(oo, _kCall);
        } else {
               return null;
        }
     }

 	//---------------------------------------------------------------------------------------------------
 	
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
							abortAbruptly(new TrackNRestartException("Unable to read status for iteration '"+ oid + "'.", e));
						} catch (SQLException e) {
							abortAbruptly(new TrackNRestartException("Unable to read status for iteration '"+ oid + "'.", e));
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
			abortAbruptly(new TrackNRestartException("No getOID() method found while program " + program + " was annotated @TrackNRestart.", e));
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
		
		track(_kCall, oo, true, null);
    }

    after(Object oo, KCall _kCall) throwing (InvocationTargetException ite) : 
    	invoke() && args(oo, ..) && cflow(enter(_kCall)) {
		
		this.result = ReturnCodes.FAILURE.name();
		track(_kCall, oo, false, ite);
    }

	//---------------------------------------------------------------------------------------------------
	
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
			abortAbruptly(new TrackNRestartException("Unable to persist status ['" + (success ? ReturnCodes.SUCCESS : ReturnCodes.FAILURE) + "'] for iteration '" + oid + "'.", e));
		} catch (SQLException e) {
			abortAbruptly(new TrackNRestartException("Unable to persist status ['" + (success ? ReturnCodes.SUCCESS : ReturnCodes.FAILURE) + "'] for iteration '" + oid + "'.", e));
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
		((X)_kCall).forEachListener.forEachInstanceComplete(oid, success);
	}

	private void abortAbruptly(Throwable t) {
		this.result = ReturnCodes.ABORTED.name();
		Programs.as((Adapter) this.root).abort();
		this.root.exceptionThrown = t;
	}

}
