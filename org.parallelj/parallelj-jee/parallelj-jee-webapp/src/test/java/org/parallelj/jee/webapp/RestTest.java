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
package org.parallelj.jee.webapp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Assert;
import org.junit.Test;
import org.parallelj.Process;
import org.parallelj.Programs;

import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.test.framework.JerseyTest;

public class RestTest extends JerseyTest {

	public RestTest() {
		super("org.parallelj.jee.webapp");
	}

	@Test
	public void instanceExists() {
		Process<SimpleProgram> instance = Programs.as(new SimpleProgram());
		Assert.assertNotNull(instance);
	}
		
	@Test
	public void testCreateEmptyProgram() {
		WebResource webResource = resource().path("/programs");
		SimpleProgram program = webResource.path("org.parallelj.jee.webapp.SimpleProgram").post(SimpleProgram.class);
		assertNotNull(program);
		assertEquals(program.getName(), SimpleProgram.DEFAULT_NAME);
	}

	@Test
	public void testCreateProgram() {
		WebResource webResource = resource().path("/programs");
		SimpleProgram program = new SimpleProgram();
		program.setName("Tester");
		program = webResource.path("org.parallelj.jee.webapp.SimpleProgram").post(SimpleProgram.class, program);
		assertNotNull(program);
		assertEquals(program.getName(), "Tester");
	}
	
}
