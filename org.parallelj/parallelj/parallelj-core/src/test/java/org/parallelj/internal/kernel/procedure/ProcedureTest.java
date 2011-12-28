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
package org.parallelj.internal.kernel.procedure;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.parallelj.internal.kernel.KInputLink;
import org.parallelj.internal.kernel.KOutputLink;
import org.parallelj.internal.kernel.KProcedure;
import org.parallelj.internal.kernel.KProcess;
import org.parallelj.internal.kernel.KProcessor;
import org.parallelj.internal.kernel.KProgram;
import org.parallelj.internal.kernel.join.KAndJoin;
import org.parallelj.internal.kernel.split.KAndSplit;

public abstract class ProcedureTest<P extends KProcedure> {

	public P procedure;

	public KProgram program;

	@Before
	public void setup() {
		this.program = new KProgram();
		this.setupProcedure();

		new KInputLink(this.program.getInputCondition(), procedure);
		new KOutputLink(procedure, this.program.getOutputCondition());

		procedure.setJoin(new KAndJoin(procedure));
		procedure.setSplit(new KAndSplit(procedure));
	}

	public abstract void setupProcedure();

	@Test
	public void test() {
		KProcess instance = program.newProcess(null);
		new KProcessor().execute(instance);
		Assert.assertFalse(program.getInputCondition().contains(instance));
		Assert.assertTrue(program.getOutputCondition().contains(instance));
	}

}
