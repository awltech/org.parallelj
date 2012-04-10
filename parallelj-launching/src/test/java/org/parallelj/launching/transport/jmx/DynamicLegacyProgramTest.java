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

import static org.junit.Assert.*;

import java.util.LinkedList;
import java.util.List;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.MBeanInfo;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.parallelj.launching.transport.tcp.program.ArgEntry;

/**
 * The class <code>DynamicLegacyProgramTest</code> contains tests for the class <code>{@link DynamicLegacyProgram}</code>.
 *
 * @generatedBy CodePro at 09/12/11 17:15
 * @author fr22240
 * @version $Revision: 1.0 $
 */
public class DynamicLegacyProgramTest {
	/**
	 * Run the DynamicLegacyProgram(Class<? extends Adapter>,List<ArgEntry>) constructor test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 09/12/11 17:15
	 */
	@Test
	public void testDynamicLegacyProgram_1()
		throws Exception {
		Class<? extends org.parallelj.internal.reflect.ProgramAdapter.Adapter> adapterClass = org.parallelj.internal.reflect.ProgramAdapter.Adapter.class;
		List<ArgEntry> adapterArgs = new LinkedList();

		DynamicLegacyProgram result = new DynamicLegacyProgram(adapterClass, adapterArgs);

		// add additional test code here
		assertNotNull(result);
	}

	/**
	 * Run the Object getAttribute(String) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 09/12/11 17:15
	 */
	@Test
	public void testGetAttribute_1()
		throws Exception {
		DynamicLegacyProgram fixture = new DynamicLegacyProgram(org.parallelj.internal.reflect.ProgramAdapter.Adapter.class, new LinkedList());
		String attribute = "";

		Object result = fixture.getAttribute(attribute);

		// add additional test code here
		assertNull(result);
	}

	/**
	 * Run the AttributeList getAttributes(String[]) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 09/12/11 17:15
	 */
	@Test
	public void testGetAttributes_1()
		throws Exception {
		DynamicLegacyProgram fixture = new DynamicLegacyProgram(org.parallelj.internal.reflect.ProgramAdapter.Adapter.class, new LinkedList());
		String[] attributes = new String[] {};

		AttributeList result = fixture.getAttributes(attributes);

		// add additional test code here
		assertNotNull(result);
	}

	/**
	 * Run the MBeanInfo getMBeanInfo() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 09/12/11 17:15
	 */
	@Test
	public void testGetMBeanInfo_1()
		throws Exception {
		DynamicLegacyProgram fixture = new DynamicLegacyProgram(org.parallelj.internal.reflect.ProgramAdapter.Adapter.class, new LinkedList());

		MBeanInfo result = fixture.getMBeanInfo();

		// add additional test code here
		assertNotNull(result);
	}

	/**
	 * Run the MBeanInfo getMBeanInfo() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 09/12/11 17:15
	 */
	@Test //(expected = java.lang.IllegalArgumentException.class)
	public void testGetMBeanInfo_2()
		throws Exception {
		DynamicLegacyProgram fixture = new DynamicLegacyProgram(org.parallelj.internal.reflect.ProgramAdapter.Adapter.class, new LinkedList());

		MBeanInfo result = fixture.getMBeanInfo();

		// add additional test code here
		assertNotNull(result);
	}

	/**
	 * Run the void setAttribute(Attribute) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 09/12/11 17:15
	 */
	@Test
	public void testSetAttribute_1()
		throws Exception {
		DynamicLegacyProgram fixture = new DynamicLegacyProgram(org.parallelj.internal.reflect.ProgramAdapter.Adapter.class, new LinkedList());
		Attribute attribute = new Attribute("", new Object());

		fixture.setAttribute(attribute);

		// add additional test code here
	}

	/**
	 * Run the AttributeList setAttributes(AttributeList) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 09/12/11 17:15
	 */
	@Test
	public void testSetAttributes_1()
		throws Exception {
		DynamicLegacyProgram fixture = new DynamicLegacyProgram(org.parallelj.internal.reflect.ProgramAdapter.Adapter.class, new LinkedList());
		AttributeList attributes = new AttributeList();

		AttributeList result = fixture.setAttributes(attributes);

		// add additional test code here
		assertNotNull(result);
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

	/**
	 * Launch the test.
	 *
	 * @param args the command line arguments
	 *
	 * @generatedBy CodePro at 09/12/11 17:15
	 */
	public static void main(String[] args) {
		new org.junit.runner.JUnitCore().run(DynamicLegacyProgramTest.class);
	}
}