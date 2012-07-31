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

import java.util.Collection;
import org.junit.*;
import static org.junit.Assert.*;
import org.quartz.Scheduler;

/**
 * The class <code>ParalleljSchedulerFactoryTest</code> contains tests for the class <code>{@link ParalleljSchedulerFactory}</code>.
 *
 * @generatedBy CodePro at 09/12/11 17:15
 * @author fr22240
 * @version $Revision: 1.0 $
 */
public class ParalleljSchedulerFactoryTest {
	/**
	 * Run the ParalleljSchedulerFactory() constructor test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 09/12/11 17:15
	 */
	@Test
	public void testParalleljSchedulerFactory_1()
		throws Exception {

		ParalleljSchedulerFactory result = new ParalleljSchedulerFactory();

		// add additional test code here
		assertNotNull(result);
	}

	/**
	 * Run the ParalleljSchedulerFactory(String) constructor test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 09/12/11 17:15
	 */
	@Test
	public void testParalleljSchedulerFactory_2()
		throws Exception {
		String fileName = "";

		ParalleljSchedulerFactory result = new ParalleljSchedulerFactory(fileName);

		// add additional test code here
		assertNotNull(result);
	}

	/**
	 * Run the Collection<Scheduler> getAllSchedulers() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 09/12/11 17:15
	 */
	@Test
	public void testGetAllSchedulers_1()
		throws Exception {
		ParalleljSchedulerFactory fixture = new ParalleljSchedulerFactory();
		fixture.getScheduler();
		Collection<Scheduler> result = fixture.getAllSchedulers();

		// add additional test code here
		assertNotNull(result);
		assertEquals(1, result.size());
	}

	/**
	 * Run the ParalleljScheduler getScheduler() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 09/12/11 17:15
	 */
	@Test
	public void testGetScheduler_1()
		throws Exception {
		ParalleljSchedulerFactory fixture = new ParalleljSchedulerFactory();

		ParalleljScheduler result = fixture.getScheduler();

		// add additional test code here
		assertNotNull(result);
		assertEquals("DefaultQuartzScheduler", result.getSchedulerName());
		assertEquals("NON_CLUSTERED", result.getSchedulerInstanceId());
		assertEquals(true, result.isInStandbyMode());
	}

	/**
	 * Run the Scheduler getScheduler(String) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 09/12/11 17:15
	 */
	@Test
	public void testGetScheduler_9()
		throws Exception {
		ParalleljSchedulerFactory fixture = new ParalleljSchedulerFactory();
		String schedName = "";

		Scheduler result = fixture.getScheduler(schedName);

		// add additional test code here
		assertEquals(null, result);
	}

	/**
	 * Perform pre-test initialization.
	 *
	 * @throws Exception
	 *         if the initialization fails for some reason
	 *
	 * @generatedBy CodePro at 09/12/11 17:15
	 */
	@Before
	public void setUp()
		throws Exception {
		// add additional set up code here
	}

	/**
	 * Perform post-test clean-up.
	 *
	 * @throws Exception
	 *         if the clean-up fails for some reason
	 *
	 * @generatedBy CodePro at 09/12/11 17:15
	 */
	@After
	public void tearDown()
		throws Exception {
		// Add additional tear down code here
	}

}