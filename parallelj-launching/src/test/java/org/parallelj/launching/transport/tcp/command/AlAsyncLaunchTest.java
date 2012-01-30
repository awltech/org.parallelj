package org.parallelj.launching.transport.tcp.command;

import org.junit.*;
import static org.junit.Assert.*;

/**
 * The class <code>AlAsyncLaunchTest</code> contains tests for the class <code>{@link AlAsyncLaunch}</code>.
 *
 * @generatedBy CodePro at 09/12/11 17:15
 * @author fr22240
 * @version $Revision: 1.0 $
 */
public class AlAsyncLaunchTest {
	/**
	 * Run the AlAsyncLaunch() constructor test.
	 *
	 * @generatedBy CodePro at 09/12/11 17:15
	 */
	@Test
	public void testAlAsyncLaunch_1()
		throws Exception {
		AlAsyncLaunch result = new AlAsyncLaunch();
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
		AlAsyncLaunch fixture = new AlAsyncLaunch();

		int result = fixture.getPriorityUsage();

		// add additional test code here
		assertEquals(69, result);
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
		AlAsyncLaunch fixture = new AlAsyncLaunch();

		String result = fixture.getType();

		// add additional test code here
		assertEquals("asl", result);
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
		AlAsyncLaunch fixture = new AlAsyncLaunch();

		String result = fixture.getUsage();

		// add additional test code here
		assertEquals("asl : Launches a new Program instance with ID x, and returns (asynchronous launch).", result);
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
		new org.junit.runner.JUnitCore().run(AlAsyncLaunchTest.class);
	}
}