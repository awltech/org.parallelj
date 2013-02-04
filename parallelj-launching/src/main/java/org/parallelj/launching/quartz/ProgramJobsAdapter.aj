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

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.parallelj.Programs;
import org.parallelj.Programs.ProcessHelper;
import org.parallelj.internal.kernel.KProcess;
import org.parallelj.internal.kernel.KProgram;
import org.parallelj.internal.kernel.procedure.CallableProcedure;
import org.parallelj.internal.kernel.procedure.RunnableProcedure;
import org.parallelj.internal.reflect.ProcessHelperImpl;
import org.parallelj.internal.reflect.ProgramAdapter.Adapter;
import org.parallelj.launching.LaunchingMessageKind;
import org.parallelj.launching.OnError;
import org.parallelj.launching.ProceduresOnError;
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
	 * Add the interface IErrorCode to the KProgram
	*/
	public interface IErrorCode {
		public String getErrorCode();
		public void setErrorCode(String errorCode);
		public Method getErrorCodeGetterMethod();
		public void setErrorCodeGetterMethod(Method getterFieldMethod);
	}
	
	public String IErrorCode.errorCode = "";
	public Method IErrorCode.getterMethod;
	
	public String IErrorCode.getErrorCode() {
		return this.errorCode;
	}
	
	public void IErrorCode.setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public void IErrorCode.setErrorCodeGetterMethod(Method getterFieldMethod) {
		this.getterMethod = getterFieldMethod;
	}

	public Method IErrorCode.getErrorCodeGetterMethod() {
		return this.getterMethod;
	}

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
	declare parents: org.parallelj.internal.kernel.KProgram implements IErrorCode;

	/*
	 * Add the interface IProceduresInError to the KProcessor
	*/
	public interface IProceduresInError {
		public void addProcedureInError(KProgram kprogram, Object program, Object proc, Exception exception);
		public ProceduresOnError getAllProceduresInError(Object program);
		public Method getGetterMethod();
		public void setGetterMethod(Method getterFieldMethod);
		public void setFieldName(String fieldName);
		public String getFieldName();
	}
	public Method IProceduresInError.getterMethod;
	public String IProceduresInError.fieldName;
	public boolean IProceduresInError.isErrors=false;
	
	public void IProceduresInError.setGetterMethod(Method getterFieldMethod) {
		this.getterMethod = getterFieldMethod;
	}

	public void IProceduresInError.setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public String IProceduresInError.getFieldName() {
		return this.fieldName;
	}
	
	public Method IProceduresInError.getGetterMethod() {
		return this.getterMethod;
	}
	
	public synchronized void IProceduresInError.addProcedureInError(KProgram kprogram, Object program, Object procedure, Exception exception) {
		ProceduresOnError obj;
		
		this.isErrors=true;
		Method method = ((IProceduresInError)kprogram).getGetterMethod();
		if (method != null) {
			try {
				obj = (ProceduresOnError)method.invoke(program, new Object[]{});
				if (obj==null) {
					obj = new ProceduresOnError();
					Field field = program.getClass().getDeclaredField(((IProceduresInError)kprogram).getFieldName());
					field.setAccessible(true);
					field.set(program, obj);
				}
				obj.addProcedureInError(procedure, exception);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}
	
	public synchronized ProceduresOnError IProceduresInError.getAllProceduresInError(Object program) {
		ProceduresOnError obj=null;
		if (this.getGetterMethod()!=null) {
			try {
				obj = (ProceduresOnError)this.getGetterMethod().invoke(program, new Object[]{});
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return obj; 
	}
	
	declare parents: org.parallelj.internal.kernel.KProgram implements IProceduresInError;

    pointcut procsOnError(Object a): get(@OnError ProceduresOnError *.*) && this(a);
    
    before(Object procOnErrorField): procsOnError(procOnErrorField) {
    }
    
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
			
			KProgram program = (((KProcess)processHelper.getProcess()).getProgram());
			IProceduresInError procedures = (IProceduresInError)program;
			
			if((procedures.getAllProceduresInError(self)!=null
					&& procedures.getAllProceduresInError(self).getNumberOfProceduresInError()>0) || procedures.isErrors) {
				jobDataMap.put(QuartzUtils.RETURN_CODE, ReturnCodes.FAILURE);
			}

			// Get the ErrorCode initialized by the user
			IErrorCode errorCodeP = (IErrorCode)programType;
			if (programType instanceof KProgram) {
				Method getterErrorMethod = errorCodeP.getErrorCodeGetterMethod();
				try {
					Object userReturnCode="";
					if(getterErrorMethod!=null) {
						// Call the getter method
						userReturnCode = getterErrorMethod.invoke(self);
						// Complete the JobDataMap
					}
					jobDataMap.put(QuartzUtils.USER_RETURN_CODE, userReturnCode);
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
			
			service.shutdown();
			if (procedures.getAllProceduresInError(self)!=null
					&& procedures.getAllProceduresInError(self).getNumberOfProceduresInError()>0){
				jobDataMap.put(QuartzUtils.PROCEDURES_IN_ERROR, procedures.getAllProceduresInError(self));
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
			((IProceduresInError)process.getProgram()).addProcedureInError(process.getProgram(), process.getContext(), runnable.getContext(), runnable.getException());
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
			((IProceduresInError)process.getProgram()).addProcedureInError(process.getProgram(), process.getContext(), callable.getContext(), callable.getException());
		}
		proceed(self);
	}
	 
	/*
	 * Allow to get the procedures in error of a Process
	 */
	public static synchronized ProceduresOnError getProceduresInErrors(
			org.parallelj.mirror.Process process) {
		return ((IProceduresInError)process.getProgram()).getAllProceduresInError(process.getContext());
	}
}
