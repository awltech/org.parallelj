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
package conf;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Test;
import org.parallelj.internal.conf.CProcedures.Procedure;
import org.parallelj.internal.conf.ConfigurationService;
import org.parallelj.internal.conf.ParalleljConfiguration;

public class ConfigurationTest {

	@Test
	public void test() {

		ParalleljConfiguration conf = (ParalleljConfiguration) ConfigurationService
				.getConfigurationService().getConfigurationManager()
				.getConfiguration();

		assertNotNull(conf);
		assertNotNull(conf.getProcedures());
		assertNotNull(conf.getProcedures().getProcedure());
		assertEquals(conf.getProcedures().getProcedure().size(), 2);

		List<Procedure> prs = conf.getProcedures().getProcedure();
		for (Procedure procedure : prs) {
			String name = procedure.getName();
			assertNotNull(name);
			if (name.equals("pr1")) {
				assertEquals(procedure.getCapacity().longValue(), 5);
			}
			if (name.equals("pr2")) {
				assertEquals(procedure.getCapacity().longValue(), 10);
			}
		}
		
		assertNotNull(conf.getServers());
		assertNotNull(conf.getServers().getJmx());
		assertEquals("localhost",conf.getServers().getJmx().getHost());
		assertEquals(9000,conf.getServers().getJmx().getPort().intValue());
		assertNotNull(conf.getServers().getTelnet());
		assertEquals("localhost",conf.getServers().getTelnet().getHost());
		assertEquals(10000,conf.getServers().getTelnet().getPort().intValue());
		assertNotNull(conf.getServers().getSsh());
		assertEquals(22,conf.getServers().getSsh().getPort().intValue());
	}

}
