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
package org.parallelj.launching.transport.jmx;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.parallelj.internal.conf.ConfigurationService;
import org.parallelj.internal.conf.ParalleljConfigurationManager;
import org.parallelj.internal.conf.pojos.ParalleljConfiguration;
import org.parallelj.launching.programs.BeginProgram;

/**
 * The class <code>JmxServerTest</code> contains tests for the class
 * <code>{@link JmxServer}</code>.
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
	public void testRegisterMBean_1() throws Exception {
		assertEquals(this.fixture.registerMBean(SampleRemote.class
				.getCanonicalName()), true);
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
		assertEquals(this.fixture.registerMBean(BeginProgram.class
				.getCanonicalName()), false);
		assertEquals(this.fixture.registerProgramAsMBean(BeginProgram.class
				.getCanonicalName()), true);
	}

	@Before
	public void tearUp() throws Exception {
		ParalleljConfiguration conf = (ParalleljConfiguration) ConfigurationService
				.getConfigurationService().getConfigurationManager()
				.get(ParalleljConfigurationManager.class).getConfiguration();
		this.host = conf.getServers().getJmx().getHost();
		this.port = conf.getServers().getJmx().getPort();

		this.fixture = new JmxServer(this.host, this.port);
		this.fixture.start();

	}

	/**
	 * Perform post-test clean-up.
	 * 
	 * @throws Exception
	 *             if the clean-up fails for some reason
	 * 
	 * @generatedBy CodePro at 09/12/11 17:15
	 */
	@After
	public void tearDown() throws Exception {
		this.fixture.stop();

	}

}