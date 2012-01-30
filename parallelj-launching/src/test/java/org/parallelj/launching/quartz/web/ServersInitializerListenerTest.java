package org.parallelj.launching.quartz.web;

import org.junit.*;
import static org.junit.Assert.*;

/**
 * The class <code>ServersInitializerListenerTest</code> contains tests for the class <code>{@link ServersInitializerListener}</code>.
 *
 * @version $Revision: 1.0 $
 */
public class ServersInitializerListenerTest {
	ServersInitializerListener fixture;
	
	/**
	 */
	@Test
	public void testServersGetTcpIpServer()
		throws Exception {
		assertNotNull(this.fixture.getTcpIpServer());
	}

	/**
	 */
	@Test
	public void testServersGetJmxServer()
		throws Exception {
		assertNotNull(this.fixture.getJmxServer());
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
		this.fixture = new ServersInitializerListener();
		this.fixture.contextInitialized(null);
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
		this.fixture.contextDestroyed(null);
	}

	/**
	 * Launch the test.
	 *
	 * @param args the command line arguments
	 *
	 * @generatedBy CodePro at 09/12/11 17:14
	 */
	public static void main(String[] args) {
		new org.junit.runner.JUnitCore().run(ServersInitializerListenerTest.class);
	}
}