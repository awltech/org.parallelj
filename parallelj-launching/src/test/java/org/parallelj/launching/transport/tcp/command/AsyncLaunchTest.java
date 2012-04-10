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
 * The class <code>AsyncLaunchTest</code> contains tests for the class <code>{@link AsyncLaunch}</code>.
 *
 * @generatedBy CodePro at 09/12/11 17:14
 * @author fr22240
 * @version $Revision: 1.0 $
 */
public class AsyncLaunchTest {
	/**
	 * Run the AsyncLaunch() constructor test.
	 *
	 * @generatedBy CodePro at 09/12/11 17:14
	 */
	@Test
	public void testAsyncLaunch_1()
		throws Exception {
		AsyncLaunch result = new AsyncLaunch();
		assertNotNull(result);
		// add additional test code here
	}

	/**
	 * Run the int getPriorityUsage() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 09/12/11 17:14
	 */
	@Test
	public void testGetPriorityUsage_1()
		throws Exception {
		AsyncLaunch fixture = new AsyncLaunch();

		int result = fixture.getPriorityUsage();

		// add additional test code here
		assertEquals(70, result);
	}

	/**
	 * Run the String getType() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 09/12/11 17:14
	 */
	@Test
	public void testGetType_1()
		throws Exception {
		AsyncLaunch fixture = new AsyncLaunch();

		String result = fixture.getType();

		// add additional test code here
		assertEquals("asynclaunch", result);
	}

	/**
	 * Run the String getUsage() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 09/12/11 17:14
	 */
	@Test
	public void testGetUsage_1()
		throws Exception {
		AsyncLaunch fixture = new AsyncLaunch();

		String result = fixture.getUsage();

		// add additional test code here
		assertEquals("asynclaunch : Launches a new Program instance, and returns (asynchronous launch).", result);
	}

	/**
	 * Run the String process(IoSession,String[]) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 09/12/11 17:14
	 */
	@Test
	public void testProcess_1()
		throws Exception {
		AsyncLaunch fixture = new AsyncLaunch();
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
	 * @generatedBy CodePro at 09/12/11 17:14
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
	 * @generatedBy CodePro at 09/12/11 17:14
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
	 * @generatedBy CodePro at 09/12/11 17:14
	 */
	public static void main(String[] args) {
		new org.junit.runner.JUnitCore().run(AsyncLaunchTest.class);
	}
}