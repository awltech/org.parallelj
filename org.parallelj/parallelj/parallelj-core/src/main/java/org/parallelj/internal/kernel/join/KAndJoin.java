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

package org.parallelj.internal.kernel.join;

import org.parallelj.internal.kernel.KCondition;
import org.parallelj.internal.kernel.KCall;
import org.parallelj.internal.kernel.KProcedure;
import org.parallelj.internal.kernel.KProcess;

/**
 * An KAndJoin is enabled if all input {@link KCondition conditions} contain
 * tokens. In such a case, it will {@link KCondition#consume(KProcess) consume}
 * a token in each input {@link KCondition conditions}.
 * 
 * @author Atos Worldline
 */
public class KAndJoin extends KAbstractJoin {

	/**
	 * Create a new and join
	 * 
	 * @param procedure
	 *            the procedure containing this join
	 */
	public KAndJoin(KProcedure procedure) {
		super(procedure);
	}

	@Override
	public void join(KCall call) {
		for (KCondition condition : this.conditions) {
			condition.consume(call.getProcess());
		}
	}

	@Override
	public boolean isEnabled(KProcess process) {
		if (this.conditions.length == 0) {
			return false;
		}
		for (KCondition condition : this.conditions) {
			if (!condition.contains(process)) {
				return false;
			}
		}
		return true;
	}

}
