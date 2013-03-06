package org.parallelj.launching.inout;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.parallelj.Programs.ProcessHelper;
import org.parallelj.internal.kernel.KProcess;
import org.parallelj.internal.kernel.KProgram;
import org.parallelj.internal.reflect.ProcessHelperImpl;
import org.parallelj.internal.reflect.ProgramAdapter.Adapter;
import org.parallelj.launching.LaunchingMessageKind;
import org.parallelj.launching.ProgramReturnCodes;
import org.parallelj.launching.internal.AbstractLaunchingListener;
import org.parallelj.launching.quartz.Launch;
import org.parallelj.launching.quartz.QuartzUtils;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class InOutLaunchingListener extends AbstractLaunchingListener {

	@Override
	public void prepareLaunching(Adapter adapter, ProcessHelper<?> processHelper, JobExecutionContext context) throws Exception {
		JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
		
		/*
		 * Arguments for the Program
		 * 
		 *  If some parameters are specified in JobDataMap, we initialize the corresponding values..
		 */
		IProgramInputOutputs programInputOutput = (IProgramInputOutputs)processHelper.getProcess().getProgram();
		@SuppressWarnings("unchecked")
		Map<String, Object> parameters = (Map<String, Object>)context.getJobDetail().getJobDataMap().get(Launch.PARAMETERS);
		for(String key:parameters.keySet()) {
			Object value = parameters.get(key);
			Argument argument = programInputOutput.getArgument(key);
			if (argument!=null) {
				argument.setValue(value);
			}
		}
		
		// Initialize fields with annotation @In
		List<Argument> arguments = programInputOutput.getArguments();
		for (Argument argument:arguments) {
			Method setter = argument.getWriteMethod();
			try {
				// Call the setter method
				setter.invoke(adapter, argument.getValue());
			} catch (IllegalAccessException e) {
				jobDataMap.put(QuartzUtils.RETURN_CODE, ProgramReturnCodes.FAILURE);
				LaunchingMessageKind.EREMOTE0010.format(argument.getName(),adapter.getClass().getCanonicalName(), e);
				throw new JobExecutionException(e);
			} catch (IllegalArgumentException e) {
				jobDataMap.put(QuartzUtils.RETURN_CODE, ProgramReturnCodes.FAILURE);
				LaunchingMessageKind.EREMOTE0010.format(argument.getName(),adapter.getClass().getCanonicalName(), e);
				throw new JobExecutionException(e);
			} catch (InvocationTargetException e) {
				jobDataMap.put(QuartzUtils.RETURN_CODE, ProgramReturnCodes.FAILURE);
				LaunchingMessageKind.EREMOTE0010.format(argument.getName(),adapter.getClass().getCanonicalName(), e);
				throw new JobExecutionException(e);
			} catch (NullPointerException e) {
				jobDataMap.put(QuartzUtils.RETURN_CODE, ProgramReturnCodes.FAILURE);
				LaunchingMessageKind.EREMOTE0010.format(argument.getName(),adapter.getClass().getCanonicalName(), e);
				throw new JobExecutionException(e);
			} catch (ExceptionInInitializerError e) {
				jobDataMap.put(QuartzUtils.RETURN_CODE, ProgramReturnCodes.FAILURE);
				LaunchingMessageKind.EREMOTE0010.format(argument.getName(),adapter.getClass().getCanonicalName(), e);
				throw new JobExecutionException(e);
			}
		}
	}

	@Override
	public void finalizeLaunching(Adapter adapter, ProcessHelper<?> processHelper, JobExecutionContext context) throws Exception {
		JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
		
		/*
		 *  If some output values are specified in the Program, we initialize the corresponding JobDataMap values..
		 *  
		 *  Initialize the Output fields of the Program as an element of "outputs" in the JobDataMap
		 *  We call the getter method of each field annotated with @Out to get its value and complete the JobDataMap
		 */
		Map<String, Object> outputs = new HashMap<String, Object>();
		for(Output output:((IProgramInputOutputs) ((KProgram) ((KProcess) ((ProcessHelperImpl<?>) processHelper)
				.getProcess()).getProgram())).getOutputs()) {
			Method getter = output.getReadMethod();
			try {
				// Call the getter method
				Object result = getter.invoke(adapter);
				// Complete the JobDataMap
				outputs.put(output.getName(), result);
			} catch (IllegalAccessException e) {
				jobDataMap.put(QuartzUtils.RETURN_CODE, ProgramReturnCodes.FAILURE);
				LaunchingMessageKind.EREMOTE0009.format(e);
				throw new JobExecutionException(e);
			} catch (IllegalArgumentException e) {
				jobDataMap.put(QuartzUtils.RETURN_CODE, ProgramReturnCodes.FAILURE);
				LaunchingMessageKind.EREMOTE0009.format(e);
				throw new JobExecutionException(e);
			} catch (InvocationTargetException e) {
				jobDataMap.put(QuartzUtils.RETURN_CODE, ProgramReturnCodes.FAILURE);
				LaunchingMessageKind.EREMOTE0009.format(e);
				throw new JobExecutionException(e);
			} catch (NullPointerException e) {
				jobDataMap.put(QuartzUtils.RETURN_CODE, ProgramReturnCodes.FAILURE);
				LaunchingMessageKind.EREMOTE0009.format(e);
				throw new JobExecutionException(e);
			} catch (ExceptionInInitializerError e) {
				jobDataMap.put(QuartzUtils.RETURN_CODE, ProgramReturnCodes.FAILURE);
				LaunchingMessageKind.EREMOTE0009.format(e);
				throw new JobExecutionException(e);
			}
		}
		
		jobDataMap.put(Launch.OUTPUTS, outputs);
		
	}

	@Override
	public int getPriority() {
		return 100;
	}

}
