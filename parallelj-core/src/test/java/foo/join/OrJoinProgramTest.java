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
package foo.join;

import org.junit.Assert;
import org.junit.Test;
import org.parallelj.Programs;
import org.parallelj.Programs.ProcessHelper;

public class OrJoinProgramTest {

	@Test
	public void all() {
		OrJoinProgram program = new OrJoinProgram();
		program.setToB(true);
		program.setToC(true);
		program.setToD(true);
		ProcessHelper<OrJoinProgram> instance = Programs.as(program);

		Assert.assertNotNull(instance);
		instance.execute();

		Assert.assertEquals(program.a, 1);
		Assert.assertEquals(program.b, 1);
		Assert.assertEquals(program.c, 1);
		Assert.assertEquals(program.d, 1);
		Assert.assertEquals(program.e, 1);
	}

	@Test
	public void c() {
		OrJoinProgram program = new OrJoinProgram();
		program.setToB(false);
		program.setToC(true);
		program.setToD(false);
		ProcessHelper<OrJoinProgram> instance = Programs.as(program);

		Assert.assertNotNull(instance);
		instance.execute();

		Assert.assertEquals(program.a, 1);
		Assert.assertEquals(program.b, 0);
		Assert.assertEquals(program.c, 1);
		Assert.assertEquals(program.d, 0);
		Assert.assertEquals(program.e, 1);
	}

	@Test
	public void fallback() {
		OrJoinProgram program = new OrJoinProgram();
		program.setToB(false);
		program.setToC(false);
		program.setToD(false);
		ProcessHelper<OrJoinProgram> instance = Programs.as(program);

		Assert.assertNotNull(instance);
		instance.execute();

		Assert.assertEquals(program.a, 1);
		Assert.assertEquals(program.b, 0);
		Assert.assertEquals(program.c, 0);
		Assert.assertEquals(program.d, 1);
		Assert.assertEquals(program.e, 1);
	}

}
