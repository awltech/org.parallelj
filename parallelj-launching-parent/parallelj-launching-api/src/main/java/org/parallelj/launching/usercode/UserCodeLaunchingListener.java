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
	public void prepareLaunching(Launch<?> launch) throws Exception {
	}

	@Override
	public void finalizeLaunching(Launch<?> launch) throws Exception {
		
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
