package org.parallelj.launching.usercode;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.parallelj.internal.kernel.KProgram;
import org.parallelj.launching.Launch;
import org.parallelj.launching.LaunchError;
import org.parallelj.launching.LaunchException;
import org.parallelj.launching.LaunchingMessageKind;
import org.parallelj.launching.ProgramReturnCodes;
import org.parallelj.launching.internal.AbstractLaunchingListener;
import org.parallelj.mirror.ProgramType;

public class UserCodeLaunchingListener extends AbstractLaunchingListener {

	@Override
	public void prepareLaunching(Launch launch) throws Exception {
	}

	@Override
	public void finalizeLaunching(Launch launch) throws Exception {
		
		/*
		 * User's return code 
		 *  
		 * Get the ErrorCode initialized by the user
		 */
		ProgramType programType = launch.getProcessHelper().getProcess().getProgram();
		IUserReturnCode errorCodeP = (IUserReturnCode)programType;
		if (programType instanceof KProgram) {
			Method getterErrorMethod = errorCodeP.getUserReturnCodeGetterMethod();
			try {
				Object userReturnCode="";
				if(getterErrorMethod!=null) {
					// Call the getter method
					userReturnCode = getterErrorMethod.invoke(launch.getJobInstance());
					// Complete the JobDataMap
				}
				launch.getLaunchResult().setReturnCode(String.valueOf(userReturnCode));
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
	}

	@Override
	public int getPriority() {
		return 80;
	}

}
