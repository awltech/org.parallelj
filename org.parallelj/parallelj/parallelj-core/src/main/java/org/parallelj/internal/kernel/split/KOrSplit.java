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

import org.parallelj.internal.kernel.KCall;
import org.parallelj.internal.kernel.KProcedure;
import org.parallelj.internal.kernel.KOutputLink;

/**
 * An KOrSplit activates all {@link KOutputLink links} with
 * {@link KOutputLink#verify(org.parallelj.internal.kernel.KProcess)} evaluated
 * to <code>true</code>.
 * 
 * If none has been activated, the last one will be activated.
 * 
 * @author Atos Worldline
 */
public class KOrSplit extends KAbstractSplit {

	/**
	 * Create a new OR split
	 * 
	 * @param procedure
	 *            the procedure containing this split
	 */
	public KOrSplit(KProcedure procedure) {
		super(procedure);
	}

	@Override
	public void split(KCall call) {
		boolean activated = false;
		for (KOutputLink link : this.links) {
			if (link.verify(call.getProcess())) {
				activated = true;
				link.activate(call.getProcess());
			}
		}
		if (!activated) {
			this.forceActivate(call.getProcess());
		}
	}
}
