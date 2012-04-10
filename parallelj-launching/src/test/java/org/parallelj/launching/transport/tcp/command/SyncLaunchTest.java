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
package org.parallelj.launching.transport.tcp.command;

import org.apache.mina.core.session.DummySession;
import org.apache.mina.core.session.IoSession;
import org.junit.*;
import static org.junit.Assert.*;

/**
 * The class <code>SyncLaunchTest</code> contains tests for the class <code>{@link SyncLaunch}</code>.
 *
 * @generatedBy CodePro at 09/12/11 17:15
 * @author fr22240
 * @version $Revision: 1.0 $
 */
public class SyncLaunchTest {
	/**
	 * Run the SyncLaunch() constructor test.
	 *
	 * @generatedBy CodePro at 09/12/11 17:15
	 */
	@Test
	public void testSyncLaunch_1()
		throws Exception {
		SyncLaunch result = new SyncLaunch();
		assertNotNull(result);
		// add additional test code here
	}

	/**
	 * Run the int getPriorityUsage() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 09/12/11 17:15
	 */
	@Test
	public void testGetPriorityUsage_1()
		throws Exception {
		SyncLaunch fixture = new SyncLaunch();

		int result = fixture.getPriorityUsage();

		// add additional test code here
		assertEquals(80, result);
	}

	/**
	 * Run the String getType() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 09/12/11 17:15
	 */
	@Test
	public void testGetType_1()
		throws Exception {
		SyncLaunch fixture = new SyncLaunch();

		String result = fixture.getType();

		// add additional test code here
		assertEquals("synclaunch", result);
	}

	/**
	 * Run the String getUsage() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 09/12/11 17:15
	 */
	@Test
	public void testGetUsage_1()
		throws Exception {
		SyncLaunch fixture = new SyncLaunch();

		String result = fixture.getUsage();

		// add additional test code here
		assertEquals("synclaunch : Launches a new Program instance and waits till return status (synchronous launch).", result);
	}

	/**
	 * Run the String process(IoSession,String[]) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 09/12/11 17:15
	 */
	@Test
	public void testProcess_1()
		throws Exception {
		SyncLaunch fixture = new SyncLaunch();
		IoSession session = new DummySession();

		String result = fixture.process(session);
		// add additional test code here
		assertEquals("Missing required option: i", result);
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

	/**
	 * Launch the test.
	 *
	 * @param args the command line arguments
	 *
	 * @generatedBy CodePro at 09/12/11 17:15
	 */
	public static void main(String[] args) {
		new org.junit.runner.JUnitCore().run(SyncLaunchTest.class);
	}
}