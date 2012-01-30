package org.parallelj.launching.quartz;

import static org.junit.Assert.fail;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.parallelj.launching.programs.BeginProgram;
import org.quartz.Job;

public class LauncherTest {

	@Test
	public void testNewLaunch() {
		try {
			Launcher launcher = Launcher.getLauncher();
			Launch launch = launcher.newLaunch(BeginProgram.class);
			launch.synchLaunch();
			Job job = launch.getAdapter();
			launcher.complete();
			
			assertNotNull(job);
			assertEquals(job.getClass(), BeginProgram.class);
			// Test Program has been executed.
			assertEquals(((BeginProgram)job).begin, true);
		} catch (LaunchException e) {
			fail("Error");
		}
	}

}
