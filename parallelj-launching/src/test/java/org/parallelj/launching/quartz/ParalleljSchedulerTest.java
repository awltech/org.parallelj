package org.parallelj.launching.quartz;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;

/**
 * The class <code>ParalleljSchedulerTest</code> contains tests for the class <code>{@link ParalleljScheduler}</code>.
 *
 * @generatedBy CodePro at 09/12/11 17:15
 * @author fr22240
 * @version $Revision: 1.0 $
 */
public class ParalleljSchedulerTest {

	@Test
	public void testGetScheduler() {
		ParalleljSchedulerFactory factory = new ParalleljSchedulerFactory();
		Scheduler scheduler = null;
		try {
			scheduler = factory.getScheduler();
		} catch (SchedulerException e) {
			fail("ParalleljShedulerFactory.getScheduler() Exception: "+e.getMessage());
		}
		
		assertNotNull(scheduler);
		assertEquals(scheduler.getClass(), ParalleljScheduler.class);
		
		
		/*
		 * A second call to ParalleljSchedulerFactory().getScheduler() must retrived
		 * the same instance of ParalleljScheduler
		 */
		Scheduler _scheduler = null;
		try {
			_scheduler = factory.getScheduler();
		} catch (SchedulerException e) {
			fail("ParalleljShedulerFactory.getScheduler() Exception: "+e.getMessage());
		}
		
		assertNotNull(scheduler);
		assertEquals(scheduler.getClass(), ParalleljScheduler.class);
		assertEquals(scheduler, _scheduler);
		try {
			assertEquals(scheduler.getSchedulerName(), _scheduler.getSchedulerName());
		} catch (SchedulerException e) {
			fail("ParalleljShedulerFactory.getScheduler().getSchedulerName() Exception: "+e.getMessage());
		}
	}
}