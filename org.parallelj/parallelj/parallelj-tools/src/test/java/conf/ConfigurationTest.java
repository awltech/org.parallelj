/*
 *     ParallelJ, framework for parallel computing
 *
 *     Copyright (C) 2010 Atos Worldline or third-party contributors as
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

import static org.junit.Assert.*;

import java.io.File;
import java.util.List;

import javax.xml.bind.JAXB;

import org.junit.Test;
import org.parallelj.tools.conf.ParalleljConfiguration;
import org.parallelj.tools.conf.CProcedures.Procedure;

public class ConfigurationTest {

	@Test
	public void test() {
		File file = new File("src/test/java/conf/parallelj.xml");
		
		ParalleljConfiguration conf = JAXB.unmarshal(file, ParalleljConfiguration.class);
		
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
	}

}
