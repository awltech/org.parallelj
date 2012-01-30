package org.parallelj.launching.transport.jmx;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.parallelj.internal.conf.ConfigurationService;
import org.parallelj.internal.conf.ParalleljConfiguration;
import org.parallelj.launching.programs.BeginProgram;

/**
 * The class <code>JmxServerTest</code> contains tests for the class <code>{@link JmxServer}</code>.
 *
 * @generatedBy CodePro at 09/12/11 17:15
 * @author fr22240
 * @version $Revision: 1.0 $
 */
public class JmxServerMBeanTest {
	private JmxServer fixture;
	private String host;
	private int port;
	
	/**
	 * Run the void registerMBean(String) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 09/12/11 17:15
	 */
	@Test
	public void testRegisterMBean_1()
		throws Exception {
		assertEquals(this.fixture.registerMBean(SampleRemote.class.getCanonicalName()), true);
	}

	/**
	 * Run the void registerMBean(String) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 09/12/11 17:15
	 */
	@Test
	public void testRegisterMBean_2() {
		assertEquals(this.fixture.registerMBean(BeginProgram.class.getCanonicalName()), false);
		assertEquals(this.fixture.registerProgramAsMBean(BeginProgram.class.getCanonicalName()), true);
	}

	@Before
	public void tearUp()
		throws Exception {
		ParalleljConfiguration conf = (ParalleljConfiguration)ConfigurationService.getConfigurationService().getConfigurationManager().getConfiguration();
		this.host = conf.getServers().getJmx().getHost();
		this.port = conf.getServers().getJmx().getPort();
		
		this.fixture = new JmxServer(this.host, this.port);
		this.fixture.start();

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
		this.fixture.stop();

	}

	/**
	 * Launch the test.
	 *
	 * @param args the command line arguments
	 *
	 * @generatedBy CodePro at 09/12/11 17:15
	 */
	public static void main(String[] args) {
		new org.junit.runner.JUnitCore().run(JmxServerMBeanTest.class);
	}
}