package org.parallelj.launching.transport.tcp;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.parallelj.internal.conf.ConfigurationService;
import org.parallelj.internal.conf.ParalleljConfiguration;

/**
 * The class <code>TcpIpServerTest</code> contains tests for the class <code>{@link TcpIpServer}</code>.
 *
 * @generatedBy CodePro at 09/12/11 17:14
 * @author fr22240
 * @version $Revision: 1.0 $
 */
public class TcpIpServerTest {
	
	private TcpIpServer fixture;
	private String host;
	private int port;
	
	@Before
	public void tearUp()
		throws Exception {
		ParalleljConfiguration conf = (ParalleljConfiguration)ConfigurationService.getConfigurationService().getConfigurationManager().getConfiguration();
		this.host = conf.getServers().getTelnet().getHost();
		this.port = conf.getServers().getTelnet().getPort();
		
		this.fixture = new TcpIpServer(this.host, this.port);
		this.fixture.start();
	}

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

		assertEquals(this.fixture.isStarted(), true);

		this.fixture.stop();
		assertEquals(this.fixture.isStarted(), false);
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