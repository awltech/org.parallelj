package org.parallelj.launching.inout;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.parallelj.internal.kernel.KProcess;
import org.parallelj.internal.kernel.KProgram;
import org.parallelj.internal.reflect.ProcessHelperImpl;
import org.parallelj.launching.Launch;
import org.parallelj.launching.LaunchError;
import org.parallelj.launching.LaunchException;
import org.parallelj.launching.LaunchingMessageKind;
import org.parallelj.launching.ProgramReturnCodes;
import org.parallelj.launching.internal.AbstractLaunchingListener;

public class InOutLaunchingListener extends AbstractLaunchingListener {

	@Override
	public void prepareLaunching(Launch launch) throws Exception {
		
		/*
		 * Arguments for the Program
		 * 
		 *  If some parameters are specified in JobDataMap, we initialize the corresponding values..
		 */
		IProgramInputOutputs programInputOutput = (IProgramInputOutputs)launch.getProcessHelper().getProcess().getProgram();
		Map<String, Object> parameters = (Map<String, Object>)launch.getInputParameters();
		if (parameters!=null) {
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
					// If there is a Parser to use, it is called first...
					if(argument.getValue()!=null) {
						argument.setValueUsingParser(String.valueOf(argument.getValue())); 
						setter.invoke(launch.getJobInstance(), argument.getValue());
					}
				} catch (IllegalAccessException e) {
					launch.getLaunchResult().setStatusCode(ProgramReturnCodes.FAILURE);
					LaunchingMessageKind.EREMOTE0010.format(argument.getName(),launch.getJobInstance().getClass().getCanonicalName(), e);
					throw new LaunchException(e);
				} catch (IllegalArgumentException e) {
					launch.getLaunchResult().setStatusCode(ProgramReturnCodes.FAILURE);
					LaunchingMessageKind.EREMOTE0010.format(argument.getName(),launch.getJobInstance().getClass().getCanonicalName(), e);
					throw new LaunchException(e);
				} catch (InvocationTargetException e) {
					launch.getLaunchResult().setStatusCode(ProgramReturnCodes.FAILURE);
					LaunchingMessageKind.EREMOTE0010.format(argument.getName(),launch.getJobInstance().getClass().getCanonicalName(), e);
					throw new LaunchException(e);
				} catch (NullPointerException e) {
					launch.getLaunchResult().setStatusCode(ProgramReturnCodes.FAILURE);
					LaunchingMessageKind.EREMOTE0010.format(argument.getName(),launch.getJobInstance().getClass().getCanonicalName(), e);
					throw new LaunchException(e);
				} catch (ExceptionInInitializerError e) {
					launch.getLaunchResult().setStatusCode(ProgramReturnCodes.FAILURE);
					LaunchingMessageKind.EREMOTE0010.format(argument.getName(),launch.getJobInstance().getClass().getCanonicalName(), e);
					throw new LaunchError(e);
				}
			}
		}
	}

	@Override
	public void finalizeLaunching(Launch launch) throws Exception {
		
		/*
		 *  If some output values are specified in the Program, we initialize the corresponding JobDataMap values..
		 *  
		 *  Initialize the Output fields of the Program as an element of "outputs" in the JobDataMap
		 *  We call the getter method of each field annotated with @Out to get its value and complete the JobDataMap
		 */
		Map<String, Object> outputs = new HashMap<String, Object>();
		for(Output output:((IProgramInputOutputs) ((KProgram) ((KProcess) ((ProcessHelperImpl<?>) launch.getProcessHelper())
				.getProcess()).getProgram())).getOutputs()) {
			Method getter = output.getReadMethod();
			try {
				// Call the getter method
				Object result = getter.invoke(launch.getJobInstance());
				// Complete the JobDataMap
				outputs.put(output.getName(), result);
			} catch (IllegalAccessException e) {
				launch.getLaunchResult().setStatusCode(ProgramReturnCodes.FAILURE);
				LaunchingMessageKind.EREMOTE0009.format(e);
				throw new LaunchException(e);
			} catch (IllegalArgumentException e) {
				launch.getLaunchResult().setStatusCode(ProgramReturnCodes.FAILURE);
				LaunchingMessageKind.EREMOTE0009.format(e);
				throw new LaunchException(e);
			} catch (InvocationTargetException e) {
				launch.getLaunchResult().setStatusCode(ProgramReturnCodes.FAILURE);
				LaunchingMessageKind.EREMOTE0009.format(e);
				throw new LaunchException(e);
			} catch (NullPointerException e) {
				launch.getLaunchResult().setStatusCode(ProgramReturnCodes.FAILURE);
				LaunchingMessageKind.EREMOTE0009.format(e);
				throw new LaunchException(e);
			} catch (ExceptionInInitializerError e) {
				launch.getLaunchResult().setStatusCode(ProgramReturnCodes.FAILURE);
				LaunchingMessageKind.EREMOTE0009.format(e);
				throw new LaunchError(e);
			}
		}
		
		launch.getLaunchResult().setOutputParameters(outputs);
		
	}

	@Override
	public int getPriority() {
		return 100;
	}

}
