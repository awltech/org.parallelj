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
package foo;

import org.junit.Assert;
import org.junit.Test;
import org.parallelj.Programs;
import org.parallelj.Programs.ProcessHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProgramsTest {
	
	static Logger logger = LoggerFactory.getLogger("org.parallelj.internal");

	@Test
	public void instanceExists() {
		ProcessHelper<MyEngine> instance = Programs.as(new MyEngine());
		Assert.assertNotNull(instance);
	}

	@Test
	public void noop() {
		ProcessHelper<NoopProgram> instance = Programs.as(new NoopProgram());
		Assert.assertNotNull(instance);
		instance.execute();
	}
	
	@Test
	public void predicate() {
		ProcessHelper<PredicateProgram> instance = Programs
				.as(new PredicateProgram());
		Assert.assertNotNull(instance);
		instance.execute();
	}

	@Test
	public void runnable() {
		ProcessHelper<RunnableProgram> instance = Programs.as(new RunnableProgram());
		Assert.assertNotNull(instance);
		instance.execute();
	}

	@Test
	public void callable() {
		ProcessHelper<CallableProgram> instance = Programs.as(new CallableProgram());
		Assert.assertNotNull(instance);
		instance.execute();
	}

	@Test
	public void subprogram() {
		ProcessHelper<SubProgramProgram> instance = Programs
				.as(new SubProgramProgram());
		Assert.assertNotNull(instance);
		instance.execute();
	}

	@Test
	public void whiledo() throws Exception {
		ProcessHelper<WhileProgram> instance = Programs.as(new WhileProgram(10));
		Assert.assertNotNull(instance);
		instance.execute();
		instance.join();
		logger.info("completed");
	}
}
