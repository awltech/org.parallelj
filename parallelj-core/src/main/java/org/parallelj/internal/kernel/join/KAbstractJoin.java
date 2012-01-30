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
import org.parallelj.internal.kernel.KInputLink;
import org.parallelj.internal.kernel.KProcedure;
import org.parallelj.internal.kernel.KJoin;
import org.parallelj.internal.kernel.KProcess;

/**
 * A join controls the enabling of a {@link KProcedure#isEnabled(KProcess)
 * procedure}.
 * 
 * The verification is perform through the {@link #isEnabled(KProcess)} method.
 * If the verification is successful, the {@link #join(KCall)} method will be
 * called.
 * 
 * @author Atos Worldline
 */
public abstract class KAbstractJoin implements KJoin {

	private KProcedure procedure;

	/**
	 * List of conditions to check.
	 */
	protected KCondition[] conditions;

	protected KAbstractJoin(KProcedure procedure) {
		this.procedure = procedure;
		this.conditions = new KCondition[procedure.getInputLinks().size()];
		int i = 0;
		for (KInputLink link : procedure.getInputLinks()) {
			this.conditions[i++] = link.getCondition();
		}
	}

	/**
	 * @return the procedure containing the join
	 */
	public KProcedure getProcedure() {
		return procedure;
	}

}
