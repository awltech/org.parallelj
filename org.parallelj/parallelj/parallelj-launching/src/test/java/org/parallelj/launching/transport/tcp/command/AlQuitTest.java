package org.parallelj.launching.transport.tcp.command;

import org.junit.*;
import static org.junit.Assert.*;

/**
 * The class <code>AlQuitTest</code> contains tests for the class <code>{@link AlQuit}</code>.
 *
 * @generatedBy CodePro at 09/12/11 17:14
 * @author fr22240
 * @version $Revision: 1.0 $
 */
public class AlQuitTest {
	/**
	 * Run the AlQuit() constructor test.
	 *
	 * @generatedBy CodePro at 09/12/11 17:14
	 */
	@Test
	public void testAlQuit_1()
		throws Exception {
		AlQuit result = new AlQuit();
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
		AlQuit fixture = new AlQuit();

		int result = fixture.getPriorityUsage();

		// add additional test code here
		assertEquals(59, result);
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
		AlQuit fixture = new AlQuit();

		String result = fixture.getType();

		// add additional test code here
		assertEquals("q", result);
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
		AlQuit fixture = new AlQuit();

		String result = fixture.getUsage();

		// add additional test code here
		assertEquals("q : Quit ", result);
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
		new org.junit.runner.JUnitCore().run(AlQuitTest.class);
	}
}