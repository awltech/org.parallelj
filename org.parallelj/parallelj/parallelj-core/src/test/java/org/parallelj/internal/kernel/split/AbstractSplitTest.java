/*
 *     ParallelJ, framework for parallel computing
 *
 *     Copyright (C) 2010 Atos Worldline or third-party contributors as
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

package org.parallelj.internal.kernel.split;

import org.junit.Assert;
import org.parallelj.internal.kernel.KCondition;
import org.parallelj.internal.kernel.KInputLink;
import org.parallelj.internal.kernel.KOutputLink;
import org.parallelj.internal.kernel.KProcedure;
import org.parallelj.internal.kernel.KProcess;
import org.parallelj.internal.kernel.KProgram;
import org.parallelj.internal.kernel.callback.Predicate;
import org.parallelj.internal.kernel.join.KAndJoin;

public abstract class AbstractSplitTest {

	private class PredicateImpl implements Predicate {

		boolean value;

		public PredicateImpl(boolean value) {
			this.value = value;
		}

		@Override
		public boolean verify(KProcess instance) {
			return this.value;
		}

	}

	KProgram program;

	KProcedure procedure;

	KOutputLink[] links;

	protected void prepare(boolean... predicates) {
		this.program = new KProgram();
		this.procedure = new KProcedure(this.program);
		new KInputLink(program.getInputCondition(), this.procedure);
		this.procedure.setJoin(new KAndJoin(this.procedure));

		this.links = new KOutputLink[predicates.length];
		for (int i = 0; i < predicates.length; i++) {
			this.links[i] = new KOutputLink(this.procedure, new KCondition(
					this.program));
			this.links[i].setPredicate(new PredicateImpl(predicates[i]));
		}
	}

	protected void verify(KProcess instance, int... values) {
		for (int i = 0; i < values.length && i < links.length; i++) {
			Assert.assertTrue(String.format("value[%d] = %d; %d expected", i,
					links[i].getCondition().size(instance), values[i]),
					links[i].getCondition().size(instance) == values[i]);
		}
	}

	protected void verifySum(KProcess instance, int sum) {
		for (KOutputLink link : this.links) {
			sum -= link.getCondition().size(instance);
		}
	}

}
