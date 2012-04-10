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