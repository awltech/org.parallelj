package org.parallelj.launching;

import org.junit.*;
import static org.junit.Assert.*;

/**
 * The class <code>LaunchingMessageKindTest</code> contains tests for the class <code>{@link LaunchingMessageKind}</code>.
 *
 * @generatedBy CodePro at 09/12/11 17:15
 * @author fr22240
 * @version $Revision: 1.0 $
 */
public class LaunchingMessageKindTest {
	/**
	 * Run the String format(Object[]) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 09/12/11 17:15
	 */
	@Test
	public void testFormat_1()
		throws Exception {
		LaunchingMessageKind fixture = LaunchingMessageKind.EJMX0001;

		String result = fixture.format();

		// add additional test code here
		assertEquals("EJMX0001\tJmxServer can't be started", result);
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
		new org.junit.runner.JUnitCore().run(LaunchingMessageKindTest.class);
	}
}