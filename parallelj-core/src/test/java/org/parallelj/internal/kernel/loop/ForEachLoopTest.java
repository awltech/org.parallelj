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
package org.parallelj.internal.kernel.loop;

import org.junit.Assert;
import org.junit.Test;
import org.parallelj.Programs;
import org.parallelj.internal.kernel.KProcedure;
import org.parallelj.internal.kernel.KProgram;

import executable.ForEachProgram;

public class ForEachLoopTest {

/*	@Test(expected = IllegalArgumentException.class)
	public void testConstructor() {
		new KForEachLoop(null, null);
	}*/

	@Test(expected = IllegalArgumentException.class)
	public void testConstructor2() {
		new KForEachLoop(new KProcedure(new KProgram()), null);
	}

/*	@Test(expected = IllegalArgumentException.class)
	public void testConstructor3() {
		new KForEachLoop(null, new KProcedure(new KProgram()));
	}
*/
	@Test
	public void testTwoLoops() {
		ForEachProgram prg = new ForEachProgram();
		Programs.as(prg).execute().join();
		Assert.assertEquals(prg.getCpt(), 6);
	}
	
}
