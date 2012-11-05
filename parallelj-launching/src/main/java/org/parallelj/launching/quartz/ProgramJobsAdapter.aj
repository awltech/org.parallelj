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
package org.parallelj.launching.quartz;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.parallelj.Programs;
import org.parallelj.Programs.ProcessHelper;
import org.parallelj.internal.kernel.KProcess;
import org.parallelj.internal.kernel.KProcessor;
import org.parallelj.internal.kernel.KProgram;
import org.parallelj.internal.kernel.procedure.CallableProcedure;
import org.parallelj.internal.kernel.procedure.RunnableProcedure;
import org.parallelj.internal.reflect.ProcessHelperImpl;
import org.parallelj.internal.reflect.ProgramAdapter.Adapter;
import org.parallelj.launching.LaunchingMessageKind;
import org.parallelj.launching.ReturnCodes;
import org.parallelj.launching.inout.Argument;
import org.parallelj.launching.inout.Output;
import org.parallelj.mirror.ProgramType;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * Implements the Job.execute(..) method
 * 
 * 
 */
privileged public aspect ProgramJobsAdapter {

	/*
	 * The Aspect JobsAdapter must be passed before this.
	 */
	declare precedence :
		org.parallelj.internal.kernel.Identifiers,
		org.parallelj.internal.reflect.ProgramAdapter,
		org.parallelj.internal.util.sm.impl.KStateMachines,
		org.parallelj.internal.util.sm.impl.KStateMachines.PerMachine,
		org.parallelj.Executables$PerExecutable,
		org.parallelj.internal.reflect.ProgramAdapter.PerProgram,
		org.parallelj.internal.log.Logs,
		org.parallelj.launching.quartz.JobsAdapter;

	/*
	 * Add the interface IProgramArguments to the KProgram
	*/
	public interface IProgramInputOutputs {
		public List<Argument> getArguments();
		public void addArgument(Argument argument);
		public Argument getArgument(String name);
		public void addOutput(Output output);
		public List<Output> getOutputs();
		public Output getOutput(String name);
	}
	
	public List<Argument> IProgramInputOutputs.arguments = new ArrayList<Argument>();
	public List<Output> IProgramInputOutputs.outputs = new ArrayList<Output>();
	
	public List<Argument> IProgramInputOutputs.getArguments() {
		return this.arguments;
	}

	public void IProgramInputOutputs.addArgument(Argument argument) {
		this.arguments.add(argument);
	}
	
	public Argument IProgramInputOutputs.getArgument(String name) {
		for (Argument argument : this.arguments) {
			if (argument.getName().equals(name)) {
				return argument;
			}
		}
		return null;
	}

	public List<Output> IProgramInputOutputs.getOutputs() {
		return this.outputs;
	}

	public void IProgramInputOutputs.addOutput(Output output) {
		this.outputs.add(output);
	}
	
	public Output IProgramInputOutputs.getOutput(String name) {
		for (Output output : this.outputs) {
			if (output.getName().equals(name)) {
				return output;
			}
		}
		return null;
	}
	
	declare parents: org.parallelj.internal.kernel.KProgram implements IProgramInputOutputs;
	
	/*
	 * Add the interface IProceduresInError to the KProcessor
	*/
	private interface IProceduresInError {
		public void addProcedureInError(String name, Exception exception);
	}
	public Map<String, Set<String>> IProceduresInError.proceduresInError=new HashMap<String, Set<String>>();
	
	public synchronized void IProceduresInError.addProcedureInError(String name, Exception exception) {
		Set<String> exceptions = ((IProceduresInError)this).proceduresInError.get(name);
		if (exceptions==null) {
			// This is the first "name" Procedure in error
			exceptions = new HashSet<String>();
		}
		if (!exceptions.contains(exception.getClass().getCanonicalName())) {
			exceptions.add(exception.getClass().getCanonicalName());
		}
		((IProceduresInError)this).proceduresInError.put(name, exceptions);
	}
	
	declare parents: org.parallelj.internal.kernel.KProcessor implements IProceduresInError;

	/**
	 * Launch a Program and initialize the Result as a JobDataMap.
	 * 
	 * @param self
	 * @param context
	 * @throws JobExecutionException
	 */
	void around(Job self, JobExecutionContext context)
			throws JobExecutionException : 
		execution( public void  Job+.execute(..) throws JobExecutionException) 
			&& (within(@org.parallelj.Program *) || within(JobsAdapter)) 
				&& args(context) && this(self) {
		JobDataMap jobDataMap = new JobDataMap();
		context.setResult(jobDataMap);
		jobDataMap.put(QuartzUtils.RETURN_CODE, ReturnCodes.SUCCESS);

		proceed(self, context);

		try {
			// Initialize an ExecutorService with the Capacity of the Program
			ProcessHelper<?> processHelper = Programs.as((Adapter) self);
			if (processHelper == null) {
				jobDataMap.put(QuartzUtils.RETURN_CODE, ReturnCodes.NOTSTARTED);
				throw new JobExecutionException(LaunchingMessageKind.ELAUNCH0003.getFormatedMessage(self));
			}
			ProgramType programType = processHelper.getProcess().getProgram();

			IProgramInputOutputs programInputOutput = (IProgramInputOutputs)programType;
			// If some parameters are specified in JobDataMap, we initialize the corresponding values..
			@SuppressWarnings("unchecked")
			Map<String, Object> parameters = (Map<String, Object>)context.getJobDetail().getJobDataMap().get(Launch.PARAMETERS);
			for(String key:parameters.keySet()) {
				Object value = parameters.get(key);
				Argument argument = programInputOutput.getArgument(key);
				if (argument!=null) {
					argument.setValue(value);
				}
			}
			
			// If an executorService was specified, we use it
			ExecutorService service = null;
			if (context.getJobDetail().getJobDataMap().get(Launch.DEFAULT_EXECUTOR_KEY) != null) {
				service = (ExecutorService)context.getJobDetail().getJobDataMap().get(Launch.DEFAULT_EXECUTOR_KEY);
			} else 
			if (programType instanceof KProgram) {
				// Initialize fields with annotation @In
				List<Argument> arguments = programInputOutput.getArguments();
				for (Argument argument:arguments) {
					Method setter = argument.getWriteMethod();
					try {
						// Call the setter method
						setter.invoke(self, argument.getValue());
					} catch (IllegalAccessException e) {
						jobDataMap.put(QuartzUtils.RETURN_CODE, ReturnCodes.FAILURE);
						LaunchingMessageKind.EREMOTE0010.format(argument.getName(),self.getClass().getCanonicalName(), e);
						throw new JobExecutionException(e);
					} catch (IllegalArgumentException e) {
						jobDataMap.put(QuartzUtils.RETURN_CODE, ReturnCodes.FAILURE);
						LaunchingMessageKind.EREMOTE0010.format(argument.getName(),self.getClass().getCanonicalName(), e);
						throw new JobExecutionException(e);
					} catch (InvocationTargetException e) {
						jobDataMap.put(QuartzUtils.RETURN_CODE, ReturnCodes.FAILURE);
						LaunchingMessageKind.EREMOTE0010.format(argument.getName(),self.getClass().getCanonicalName(), e);
						throw new JobExecutionException(e);
					} catch (NullPointerException e) {
						jobDataMap.put(QuartzUtils.RETURN_CODE, ReturnCodes.FAILURE);
						LaunchingMessageKind.EREMOTE0010.format(argument.getName(),self.getClass().getCanonicalName(), e);
						throw new JobExecutionException(e);
					} catch (ExceptionInInitializerError e) {
						jobDataMap.put(QuartzUtils.RETURN_CODE, ReturnCodes.FAILURE);
						LaunchingMessageKind.EREMOTE0010.format(argument.getName(),self.getClass().getCanonicalName(), e);
						throw new JobExecutionException(e);
					}
				}
				
				// Initialize an ExecutorService with the Program Capacity
				short programCapacity = ((KProgram) programType)
						.getCapacity();
				service = (programCapacity == Short.MAX_VALUE) ? Executors
						.newCachedThreadPool() : Executors
						.newFixedThreadPool(programCapacity);
			} else {
				service = Executors
						.newCachedThreadPool();
			}

			// Launch the program with the initialized ExecutorService
			processHelper.execute(service).join();
			
			// Initialize the Output fields of the Program as an element of "outputs" in the JobDataMap
			// We call the getter method of each field annotated with @Out to get its value and complete the JobDataMap 
			Map<String, Object> outputs = new HashMap<String, Object>();
			for(Output output:((IProgramInputOutputs) ((KProgram) ((KProcess) ((ProcessHelperImpl<?>) processHelper)
					.getProcess()).getProgram())).getOutputs()) {
				Method getter = output.getReadMethod();
				try {
					// Call the getter method
					Object result = getter.invoke(self);
					// Complete the JobDataMap
					outputs.put(output.getName(), result);
				} catch (IllegalAccessException e) {
					jobDataMap.put(QuartzUtils.RETURN_CODE, ReturnCodes.FAILURE);
					LaunchingMessageKind.EREMOTE0009.format(e);
					throw new JobExecutionException(e);
				} catch (IllegalArgumentException e) {
					jobDataMap.put(QuartzUtils.RETURN_CODE, ReturnCodes.FAILURE);
					LaunchingMessageKind.EREMOTE0009.format(e);
					throw new JobExecutionException(e);
				} catch (InvocationTargetException e) {
					jobDataMap.put(QuartzUtils.RETURN_CODE, ReturnCodes.FAILURE);
					LaunchingMessageKind.EREMOTE0009.format(e);
					throw new JobExecutionException(e);
				} catch (NullPointerException e) {
					jobDataMap.put(QuartzUtils.RETURN_CODE, ReturnCodes.FAILURE);
					LaunchingMessageKind.EREMOTE0009.format(e);
					throw new JobExecutionException(e);
				} catch (ExceptionInInitializerError e) {
					jobDataMap.put(QuartzUtils.RETURN_CODE, ReturnCodes.FAILURE);
					LaunchingMessageKind.EREMOTE0009.format(e);
					throw new JobExecutionException(e);
				}
			}
			
			jobDataMap.put(Launch.OUTPUTS, outputs);
			
			KProcessor rootProcessor = ((KProcessor)processHelper.getProcess().getProcessor());
			IProceduresInError procedures = (IProceduresInError)rootProcessor;
			if(procedures.proceduresInError.size()>0) {
				jobDataMap.put(QuartzUtils.RETURN_CODE, ReturnCodes.FAILURE);
			}
		
			service.shutdown();
			
			if (procedures.proceduresInError.size()>0){
				jobDataMap.put(QuartzUtils.PROCEDURES_IN_ERROR, procedures.proceduresInError);
				throw new JobExecutionException();
			}
		} catch (Exception e) {
			jobDataMap.put(QuartzUtils.RETURN_CODE, ReturnCodes.FAILURE);
			throw new JobExecutionException(e);
		}
	}
	
	// Interception around Runnable execution      
	void around(Object self): call(void complete())
		&& target(org.parallelj.internal.kernel.procedure.RunnableProcedure.RunnableCall)
		&& this(self)  {
		 
		RunnableProcedure.RunnableCall runnable = (RunnableProcedure.RunnableCall)thisJoinPoint.getTarget();
		RunnableProcedure procedure = (RunnableProcedure)runnable.getProcedure();
		if (procedure.getHandler() == null
				&& runnable.getException() != null) {
			// There is an error !!!
			KProcess process = runnable.getProcess();
			KProcessor rootProcessor = process.getProcessor();//.getRootProcessor();
			((IProceduresInError)rootProcessor).addProcedureInError(procedure.getType(), runnable.getException());
		}
		proceed(self);
	}

	// Interception around Callable execution      
	void around(Object self): call(void complete())
		&& target(org.parallelj.internal.kernel.procedure.CallableProcedure.CallableCall)
		&& this(self)  {

		CallableProcedure.CallableCall callable = (CallableProcedure.CallableCall)thisJoinPoint.getTarget();
		CallableProcedure procedure = (CallableProcedure)callable.getProcedure(); 
			
		if (
				procedure.getHandler() == null
				&& callable.getException() != null
				) {
			// There is an error !!!
			KProcess process = callable.getProcess();
			KProcessor rootProcessor = process.getProcessor();//.getRootProcessor();
			((IProceduresInError)rootProcessor).addProcedureInError(procedure.getType(), callable.getException());
		}
		proceed(self);
	}
	 
	/*
	 * Allow to get the procedures in error of a Process (the root Process !!!)
	 */
	public static synchronized Map<String, Set<String>> getProceduresInErrors(
			org.parallelj.mirror.Process process) {
		KProcessor rootProcessor = ((KProcessor) process.getProcessor());
		IProceduresInError procedures = (IProceduresInError) rootProcessor;
		return procedures.proceduresInError;
	}
}
