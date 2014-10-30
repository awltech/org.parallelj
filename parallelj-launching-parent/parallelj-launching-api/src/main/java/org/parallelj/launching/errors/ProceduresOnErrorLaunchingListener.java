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
