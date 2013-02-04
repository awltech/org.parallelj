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
package jaxb;

import java.io.StringReader;

import javax.xml.bind.JAXB;

import org.junit.Assert;
import org.junit.Test;
import org.parallelj.Programs;
import org.parallelj.Programs.ProcessHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class XProgramTest {
	
	static Logger logger = LoggerFactory.getLogger("org.parallelj.internal");
	
	@Test
	public void simple() {
		ProcessHelper<XProgram> process = Programs.as(new XProgram());
		process.context().name = "aaa";
		process.execute();
		
		Assert.assertEquals(process.context().name, "AAA");
		JAXB.marshal(process.context(), System.out);
	}
	
	@Test
	public void jaxb() {
		XProgram program = JAXB.unmarshal(new StringReader("<xProgram name='aaa'/>"), XProgram.class);
		ProcessHelper<XProgram> process = Programs.as(program);
		
		process.execute();
		Assert.assertEquals(program.name, "AAA");
	}

}
