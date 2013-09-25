package org.parallelj.launching.errors;

import org.parallelj.internal.kernel.KProcess;
import org.parallelj.internal.kernel.KProgram;
import org.parallelj.launching.Launch;
import org.parallelj.launching.ProgramReturnCodes;
import org.parallelj.launching.internal.AbstractLaunchingListener;

public class ProceduresOnErrorLaunchingListener extends AbstractLaunchingListener {

	@Override
	public void prepareLaunching(Launch<?> launch) throws Exception {
	}

	@Override
	public void finalizeLaunching(Launch<?> launch) throws Exception {
		
		/*
		 * User's return code 
		 *  
		 * Get the ErrorCode initialized by the user
		 */
		KProgram program = (((KProcess)launch.getProcessHelper().getProcess()).getProgram());
		IProceduresOnError procedures = (IProceduresOnError)program;
		
		boolean isProcedureOnError=procedures.getAllProceduresInError(launch.getJobInstance())!=null
				&& procedures.getAllProceduresInError(launch.getJobInstance()).getNumberOfProceduresInError()>0;
				
		if(isProcedureOnError || procedures.isError()) {
			launch.getLaunchResult().setStatusCode(ProgramReturnCodes.FAILURE);
			
			/*
			 * Procedures on errors
			 * 		
			 */
			if (isProcedureOnError){
				launch.getLaunchResult().setProceduresInError(procedures.getAllProceduresInError(launch.getJobInstance()));
			}
		}

	}

	@Override
	public int getPriority() {
		return 60;
	}

}
