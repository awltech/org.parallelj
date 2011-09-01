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

import org.parallelj.internal.kernel.KCall;
import org.parallelj.internal.kernel.KProcedure;
import org.parallelj.internal.kernel.KOutputLink;

/**
 * An KAndSplit activates all the links.
 * 
 * The {@link KOutputLink#verify(org.parallelj.internal.kernel.KProcess)} is
 * bypassed.
 * 
 * @author Atos Worldline
 */
public class KAndSplit extends KAbstractSplit {

	/**
	 * Create a new AndSplit
	 * 
	 * @param procedure
	 *            the procedure containing this split
	 */
	public KAndSplit(KProcedure procedure) {
		super(procedure);
	}

	@Override
	public void split(KCall call) {
		for (KOutputLink link : this.links)
			link.activate(call.getProcess());
	}
}
