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

package org.parallelj.internal.kernel.callback;

import org.parallelj.internal.kernel.KProcess;
import org.parallelj.internal.kernel.KSplit;

/**
 * A {@link Predicate} is a constraint used especially during {@link KSplit}
 * execution.
 * 
 * @author Atos Worldline
 */
public interface Predicate {

	/**
	 * Verify the predicate.
	 * 
	 * @param process
	 *            the process
	 * @return <code>true</code> if the predicate is valid.
	 */
	public boolean verify(KProcess process);

}
