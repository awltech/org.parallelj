package org.parallelj.internal.kernel;

import junit.framework.Assert;

import org.junit.Test;
import org.parallelj.mirror.ProcessState;

public class ProcessTest {

	/**
	 * Test that a process with no enabled transition is in COMPLETED state when
	 * start is called.
	 * 
	 */
	@Test
	public void noProcedure() {
		KProcess process = new KProgram().newProcess(null);
		Assert.assertTrue(process.getState() == ProcessState.PENDING);
		process.start();
		Assert.assertTrue(process.getState() == ProcessState.COMPLETED);
	}

}
