package org.parallelj.launching.transport.tcp.command;

import org.junit.*;
import static org.junit.Assert.*;

/**
 * The class <code>AlHelpTest</code> contains tests for the class <code>{@link AlHelp}</code>.
 *
 * @generatedBy CodePro at 09/12/11 17:15
 * @author fr22240
 * @version $Revision: 1.0 $
 */
public class AlHelpTest {
	/**
	 * Run the AlHelp() constructor test.
	 *
	 * @generatedBy CodePro at 09/12/11 17:15
	 */
	@Test
	public void testAlHelp_1()
		throws Exception {
		AlHelp result = new AlHelp();
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
		AlHelp fixture = new AlHelp();

		int result = fixture.getPriorityUsage();

		// add additional test code here
		assertEquals(99, result);
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
		AlHelp fixture = new AlHelp();

		String result = fixture.getType();

		// add additional test code here
		assertEquals("h", result);
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
		AlHelp fixture = new AlHelp();

		String result = fixture.getUsage();

		// add additional test code here
		assertEquals("h : Print this help message", result);
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
		new org.junit.runner.JUnitCore().run(AlHelpTest.class);
	}
}