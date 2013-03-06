package org.parallelj.launching.usercode;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.parallelj.Programs.ProcessHelper;
import org.parallelj.internal.kernel.KProgram;
import org.parallelj.internal.reflect.ProgramAdapter.Adapter;
import org.parallelj.launching.LaunchingMessageKind;
import org.parallelj.launching.ProgramReturnCodes;
import org.parallelj.launching.internal.AbstractLaunchingListener;
import org.parallelj.launching.quartz.QuartzUtils;
import org.parallelj.mirror.ProgramType;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class UserCodeLaunchingListener extends AbstractLaunchingListener {

	@Override
	public void prepareLaunching(Adapter adapter, ProcessHelper<?> processHelper, JobExecutionContext context) throws Exception {
		JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
	}

	@Override
	public void finalizeLaunching(Adapter adapter, ProcessHelper<?> processHelper, JobExecutionContext context) throws Exception {
		JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
		
		/*
		 * User's return code 
		 *  
		 * Get the ErrorCode initialized by the user
		 */
		ProgramType programType = processHelper.getProcess().getProgram();
		IUserReturnCode errorCodeP = (IUserReturnCode)programType;
		if (programType instanceof KProgram) {
			Method getterErrorMethod = errorCodeP.getUserReturnCodeGetterMethod();
			try {
				Object userReturnCode="";
				if(getterErrorMethod!=null) {
					// Call the getter method
					userReturnCode = getterErrorMethod.invoke(adapter);
					// Complete the JobDataMap
				}
				jobDataMap.put(QuartzUtils.USER_RETURN_CODE, userReturnCode);
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
	}

	@Override
	public int getPriority() {
		return 80;
	}

}
