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
package org.parallelj.internal.kernel.join;

import org.parallelj.internal.kernel.KCondition;
import org.parallelj.internal.kernel.KCall;
import org.parallelj.internal.kernel.KProcedure;
import org.parallelj.internal.kernel.KProcess;

/**
 * An KXorJoin is enabled if at least one of the input {@link KCondition}
 * contains tokens. In such a case, it will consume one token at a time.
 * 
 * @author Atos Worldline
 * 
 */
public class KXorJoin extends KAbstractJoin {

	/**
	 * Create a new XOR join
	 * 
	 * @param procedure
	 *            the procedure containing this join
	 */
	public KXorJoin(KProcedure procedure) {
		super(procedure);

	}

	@Override
	public void join(KCall call) {
		for (KCondition condition : this.conditions) {
			if (condition.contains(call.getProcess())) {
				condition.consume(call.getProcess());
				return;
			}
		}
	}

	@Override
	public boolean isEnabled(KProcess process) {
		for (KCondition condition : this.conditions) {
			if (condition.contains(process)) {
				return true;
			}
		}
		return false;
	}

}
