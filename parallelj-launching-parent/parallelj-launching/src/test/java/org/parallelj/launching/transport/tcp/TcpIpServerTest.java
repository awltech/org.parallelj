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
package org.parallelj.launching.transport.tcp;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.parallelj.internal.conf.ConfigurationService;
import org.parallelj.internal.conf.ParalleljConfigurationManager;
import org.parallelj.internal.conf.pojos.ParalleljConfiguration;

/**
 * The class <code>TcpIpServerTest</code> contains tests for the class
 * <code>{@link TcpIpServer}</code>.
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
	public void tearUp() throws Exception {
		ParalleljConfiguration conf = (ParalleljConfiguration) ConfigurationService
				.getConfigurationService().getConfigurationManager()
				.get(ParalleljConfigurationManager.class).getConfiguration();
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
	public void testTcpIpServer_1() throws Exception {

		assertEquals(this.fixture.isStarted(), true);

		this.fixture.stop();
		assertEquals(this.fixture.isStarted(), false);
	}

}