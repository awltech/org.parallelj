package org.parallelj.launching.errors;

import org.parallelj.Programs.ProcessHelper;
import org.parallelj.internal.kernel.KProcess;
import org.parallelj.internal.kernel.KProgram;
import org.parallelj.internal.reflect.ProgramAdapter.Adapter;
import org.parallelj.launching.ProgramReturnCodes;
import org.parallelj.launching.internal.AbstractLaunchingListener;
import org.parallelj.launching.quartz.QuartzUtils;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;

public class ProceduresOnErrorLaunchingListener extends AbstractLaunchingListener {

	@Override
	public void prepareLaunching(Adapter adapter, ProcessHelper<?> processHelper, JobExecutionContext context) throws Exception {
		//JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
	}

	@Override
	public void finalizeLaunching(Adapter adapter, ProcessHelper<?> processHelper, JobExecutionContext context) throws Exception {
		JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
		
		/*
		 * User's return code 
		 *  
		 * Get the ErrorCode initialized by the user
		 */
		KProgram program = (((KProcess)processHelper.getProcess()).getProgram());
		IProceduresOnError procedures = (IProceduresOnError)program;
		
		boolean isProcedureOnError=procedures.getAllProceduresInError(adapter)!=null
				&& procedures.getAllProceduresInError(adapter).getNumberOfProceduresInError()>0;
				
		if(isProcedureOnError || procedures.isError()) {
			jobDataMap.put(QuartzUtils.RETURN_CODE, ProgramReturnCodes.FAILURE);
			
			/*
			 * Procedures on errors
			 * 		
			 */
			if (isProcedureOnError){
				jobDataMap.put(QuartzUtils.PROCEDURES_IN_ERROR, procedures.getAllProceduresInError(adapter));
			}
		}

	}

	@Override
	public int getPriority() {
		return 60;
	}

}
