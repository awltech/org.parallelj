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
package org.parallelj.launching.programs;

import org.junit.Assert;
import org.junit.Test;
import org.parallelj.Programs;
import org.parallelj.Programs.ProcessHelper;
import org.parallelj.launching.Launch;
import org.parallelj.launching.LaunchException;
import org.parallelj.launching.Launcher;
import org.parallelj.launching.errors.IProceduresOnError;
import org.parallelj.launching.errors.ProceduresOnError;
import org.parallelj.mirror.ProgramType;

public class ListenersTest {
	@Test
	public void testProgramWithError() {
		ProgramWithErrors prg = new ProgramWithErrors();
		ProcessHelper<ProgramWithErrors> ph = Programs.as(prg).execute().join();
		Assert.assertNotNull(prg.getOnMynErrors());
		ProgramType prgt = ph.getProcess().getProgram();
		Assert.assertTrue(prgt instanceof IProceduresOnError);
		Assert.assertTrue(((IProceduresOnError)prgt).isError());
		Assert.assertTrue(((IProceduresOnError)prgt).isHandledError());
		ProceduresOnError procOnError = prg.getOnMynErrors();
		Assert.assertNotNull(procOnError.getProceduresInError());
		Assert.assertEquals(procOnError.getProceduresInError().size(), 2);
		Assert.assertNotNull(procOnError.getProceduresHandledInError());
		Assert.assertEquals(procOnError.getProceduresHandledInError().size(), 1);
	}

	@Test
	public void testProgramWithoutError() {
		ProgramWithoutErrors prg = new ProgramWithoutErrors();
		Programs.as(prg).execute().join();
		Assert.assertNull(prg.getOnMynErrors());
	}

	@Test
	public void testProgramWithUserErrorCode() {
		Launch<ProgramWithUserErrorCode> launch;
		try {
			launch = Launcher.getLauncher().newLaunch(ProgramWithUserErrorCode.class);
			launch.synchLaunch();
			Assert.assertNotNull(launch.getLaunchResult().getReturnCode());
			Assert.assertEquals(launch.getLaunchResult().getReturnCode(), "USER_RETURN_CODE");
		} catch (LaunchException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testProgramWithoutErrorInOut() {
		Launch<ProgramWithoutErrors> launch;
		try {
			launch = Launcher.getLauncher().newLaunch(ProgramWithoutErrors.class);
			launch.addParameter("in", "InValue");
			launch.synchLaunch();
			Assert.assertNotNull(launch.getJobInstance());
			Assert.assertNotNull(launch.getJobInstance().getOut());
			Assert.assertEquals(launch.getJobInstance().getOut(), "out_InValue");
		} catch (LaunchException e) {
			e.printStackTrace();
		}
	}

}
