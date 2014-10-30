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
package org.parallelj.launching.quartz.web;

import org.junit.*;
import org.parallelj.launching.web.LegacyServersInitializerListener;

import static org.junit.Assert.*;

/**
 * The class <code>ServersInitializerListenerTest</code> contains tests for the class <code>{@link ServersInitializerListener}</code>.
 *
 * @version $Revision: 1.0 $
 */
public class ServersInitializerListenerTest {
	LegacyServersInitializerListener fixture;
	
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
		this.fixture = new LegacyServersInitializerListener();
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

}