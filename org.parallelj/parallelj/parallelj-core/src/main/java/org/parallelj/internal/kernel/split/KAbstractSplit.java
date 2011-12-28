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
package org.parallelj.internal.kernel.split;

import org.parallelj.internal.kernel.KProcedure;
import org.parallelj.internal.kernel.KOutputLink;
import org.parallelj.internal.kernel.KProcess;
import org.parallelj.internal.kernel.KSplit;

/**
 * 
 * 
 * @author Atos Worldline
 */
public abstract class KAbstractSplit implements KSplit {

	private KProcedure procedure;

	protected KOutputLink[] links;

	/**
	 * Create a new abstract split
	 * 
	 * @param procedure
	 *            the procedure that contains this split
	 */
	public KAbstractSplit(KProcedure procedure) {
		this.procedure = procedure;
		this.links = procedure.getOutputLinks().toArray(new KOutputLink[0]);
	}

	/**
	 * Force the activation of the last link.
	 * 
	 * 
	 */
	protected void forceActivate(KProcess process) {
		this.links[this.links.length - 1].activate(process);
	}

	/**
	 * @return the procedure containing this split
	 */
	public KProcedure getProcedure() {
		return procedure;
	}
}
