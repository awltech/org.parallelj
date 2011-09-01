package org.parallelj.internal.kernel;

import org.junit.Assert;
import org.junit.Test;
import org.parallelj.mirror.ProcessorState;


public class KProcessorTest {
	
	@Test
	public void test() {
		
		KProcessor processor = new KProcessor();
		Assert.assertTrue(processor.getState() == ProcessorState.PENDING);
		processor.suspend();
		Assert.assertTrue(processor.getState() == ProcessorState.SUSPENDED);
		
		processor.execute(new KProgram().newProcess(null));
		Assert.assertTrue(processor.getState() == ProcessorState.SUSPENDED);
		processor.resume();
		Assert.assertTrue(processor.getState() == ProcessorState.PENDING);
		
	}

}
