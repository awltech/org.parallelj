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
package tutorial;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.concurrent.Executors;

import org.junit.Assert;
import org.junit.Test;
import org.parallelj.Programs;
import org.parallelj.Programs.ProcessHelper;
import org.parallelj.internal.kernel.KProcedure;
import org.parallelj.internal.kernel.KProgram;
import org.parallelj.internal.kernel.procedure.SubProcessProcedure;
import org.parallelj.mirror.Procedure;

public class PipelineTutorialTest {

	@Test
	public void test() {
		
		// initialize the program
		MyPipelineTest tutorial = new MyPipelineTest();
		tutorial.setList(Arrays.asList("a", "b", "c", "d", "e"/*, "f", "g", "h",
				"i", "j"*//*, "k", "l"*/));

		// run the program with a cached thread pool
		// wait for the completion of the program: Programs.join()
		
		// Test Capacities from parallelj.xml
		ProcessHelper<MyPipelineTest> prgTest = Programs.as(tutorial).execute(Executors.newFixedThreadPool(10)).join();
		short prgCapacity = ((KProgram)prgTest.getProcess().getProgram()).getCapacity();
		assertEquals(prgCapacity, 25);
		
		for(Procedure procedure:prgTest.getProcess().getProgram().getProcedures() ) {
			if (procedure.getClass().equals(MyInnerProgram.class)) {
				assertEquals(((SubProcessProcedure)procedure).getSubProgram().getCapacity(), 12);
				for(Procedure subProcedure:((SubProcessProcedure)procedure).getSubProgram().getProcedures()) {
					if (subProcedure.getName().equals("procone")) {
					 assertEquals(((KProcedure)subProcedure).getCapacity(),3);
					}
				}
			}
		}
		
		// check that all values are in upper case
		for (String s : tutorial.getList()) {
			Assert.assertEquals(s, s);
		}
	}

}
