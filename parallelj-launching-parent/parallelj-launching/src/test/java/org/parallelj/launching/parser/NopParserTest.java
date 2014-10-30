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
package org.parallelj.launching.parser;

import org.junit.*;
import static org.junit.Assert.*;

/**
 * The class <code>NopParserTest</code> contains tests for the class <code>{@link NopParser}</code>.
 *
 * @generatedBy CodePro at 09/12/11 17:15
 * @author fr22240
 * @version $Revision: 1.0 $
 */
public class NopParserTest {
	/**
	 * Run the Object parse(String) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 09/12/11 17:15
	 */
	@Test
	public void testParse_1()
		throws Exception {
		NopParser fixture = new NopParser();
		String value = "theresult";

		Object result = fixture.parse(value);

		// add additional test code here
		assertNotNull(result);
		assertEquals(value, result);
	}

	/**
	 * Perform pre-test initialization.
	 *
	 * @throws Exception
	 *         if the initialization fails for some reason
	 *
	 * @generatedBy CodePro at 09/12/11 17:15
	 */
	@Before
	public void setUp()
		throws Exception {
		// add additional set up code here
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
		// Add additional tear down code here
	}

}