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
package org.parallelj.launching.internal.ext;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;


public class ExtensionsServiceTest {
	
	private List<Extension> extensions;
	
	@Test
	public void test_1()
		throws Exception {
		
		assertNotNull(this.extensions);
		assertEquals(this.extensions.size(), 2);
		assertEquals(this.extensions.size(), 2);
		
		for (Extension extension : this.extensions) {
			assertNotNull(extension.getType());
			if (extension.getType().equalsIgnoreCase("t1")) {
				assertTrue(extension.isInitialized());
			} else if (extension.getType().equalsIgnoreCase("t1")) {
				assertFalse(extension.isInitialized());
			}
		}
	}

	@Before
	public void setUp()
		throws Exception {
		this.extensions =  ExtensionService.getExtensionService().getExtensions();
	}

	@After
	public void tearDown()
		throws Exception {
	}

}