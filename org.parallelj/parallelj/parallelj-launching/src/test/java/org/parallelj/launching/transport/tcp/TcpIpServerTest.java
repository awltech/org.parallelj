package org.parallelj.launching.transport.tcp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

/**
 * The class <code>TcpIpServerTest</code> contains tests for the class <code>{@link TcpIpServer}</code>.
 *
 * @generatedBy CodePro at 09/12/11 17:14
 * @author fr22240
 * @version $Revision: 1.0 $
 */
public class TcpIpServerTest {
	
	/**
	 * Run the TcpIpServer(String,int) constructor test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 09/12/11 17:14
	 */
	@Test
	public void testTcpIpServer_1()
		throws Exception {

		TcpIpServer result = new TcpIpServer("localhost", 10002);
		result.start();
		assertEquals(result.isStarted(), true);
		result.stop();
		assertEquals(result.isStarted(), false);

		// add additional test code here
		assertNotNull(result);
	}

	/**
	 * Launch the test.
	 *
	 * @param args the command line arguments
	 *
	 * @generatedBy CodePro at 09/12/11 17:14
	 */
	public static void main(String[] args) {
		new org.junit.runner.JUnitCore().run(TcpIpServerTest.class);
	}
}