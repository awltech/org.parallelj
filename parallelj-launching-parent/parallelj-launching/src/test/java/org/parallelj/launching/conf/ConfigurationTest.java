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
package org.parallelj.launching.conf;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.parallelj.internal.conf.ConfigurationService;
import org.parallelj.internal.conf.ParalleljConfigurationManager;
import org.parallelj.internal.conf.pojos.CExecutor;
import org.parallelj.internal.conf.pojos.ParalleljConfiguration;
import org.parallelj.internal.conf.pojos.Threadpooltype;

public class ConfigurationTest {

	@Test
	public void test() {

		ParalleljConfiguration conf = (ParalleljConfiguration) ConfigurationService
				.getConfigurationService().getConfigurationManager()
				.get(ParalleljConfigurationManager.class).getConfiguration();

		assertNotNull(conf.getExecutorServices());
		assertNotNull(conf.getExecutorServices().getExecutorService());
		assertEquals(conf.getExecutorServices().getExecutorService().size(), 4);
		assertNotNull(conf.getExecutorServices().getExecutorService().get(0));
		assertNotNull(conf.getExecutorServices().getExecutorService().get(0).getProgramName().equals("net.atos.myapp.Program1"));
		assertNotNull(conf.getExecutorServices().getExecutorService().get(1));
		assertNotNull(conf.getExecutorServices().getExecutorService().get(1).getProgramName().equals("net.atos.myapp.Program2"));
		assertNotNull(conf.getExecutorServices().getExecutorService().get(2));
		assertNotNull(conf.getExecutorServices().getExecutorService().get(2).getProgramName().equals("net.atos.myapp.Program3"));
		assertNotNull(conf.getExecutorServices().getExecutorService().get(3));
		assertNotNull(conf.getExecutorServices().getExecutorService().get(3).getProgramName().equals("net.atos.myapp.Program4"));
		for (CExecutor exec : conf.getExecutorServices().getExecutorService()) {
			if (exec.getProgramName().equals("org.parallelj.Program1")) {
				assertNotNull(exec.getServiceType());
				assertTrue(exec.getServiceType() == Threadpooltype.FIXED_THREAD_POOL); 
				assertNotNull(exec.getPoolSize());
				assertTrue(exec.getPoolSize().intValue() == 10); 
			} else if (exec.getProgramName().equals("org.parallelj.Program2")) {
				assertNotNull(exec.getServiceType());
				assertTrue(exec.getServiceType() == Threadpooltype.CACHED_THREAD_POOL); 
				assertNull(exec.getPoolSize());
			} else if (exec.getProgramName().equals("org.parallelj.Program3")) {
				assertNotNull(exec.getServiceType());
				assertTrue(exec.getServiceType() == Threadpooltype.SINGLE_THREAD_EXECUTOR); 
			} else if (exec.getProgramName().equals("org.parallelj.Program4")) {
				assertNotNull(exec.getServiceType());
				assertTrue(exec.getServiceType() == Threadpooltype.PROVIDED);
				assertEquals(exec.getServiceClass(), "conf.TheServiceClass");
			}
		}
}
	

}
