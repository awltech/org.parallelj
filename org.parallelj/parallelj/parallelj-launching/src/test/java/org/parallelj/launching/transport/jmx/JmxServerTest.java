package org.parallelj.launching.transport.jmx;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Test;

/**
 * The class <code>JmxServerTest</code> contains tests for the class <code>{@link JmxServer}</code>.
 *
 * @generatedBy CodePro at 09/12/11 17:15
 * @author fr22240
 * @version $Revision: 1.0 $
 */
public class JmxServerTest {
	/**
	 * Run the JmxServer(String,int) constructor test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 09/12/11 17:15
	 */
	@Test
	public void testJmxServer_1()
		throws Exception {
		JmxServer fixture = new JmxServer("localhost", 10032);
		fixture.start();
		assertEquals(fixture.isStarted(), true);
		fixture.stop();
		assertEquals(fixture.isStarted(), false);
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
		new org.junit.runner.JUnitCore().run(JmxServerTest.class);
	}
}