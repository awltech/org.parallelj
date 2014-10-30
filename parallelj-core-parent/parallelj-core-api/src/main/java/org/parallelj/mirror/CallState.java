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
package org.parallelj.mirror;

import org.parallelj.internal.util.sm.Pseudostate;
import org.parallelj.internal.util.sm.PseudostateKind;

/**
 * Represents the possible state of a {@link Call}.
 * 
 * @author Laurent Legrand
 * @since 0.5.0
 * 
 */
public enum CallState {
	/**
	 * The {@link Call} is ready to be started.
	 */
	@Pseudostate(kind = PseudostateKind.INITIAL)
	PENDING,

	/**
	 * The {@link Call} is being executed.
	 */
	RUNNING,

	/**
	 * The execution of the {@link Call} is completed either successfully or with exception.
	 */
	COMPLETED,

	/**
	 * The {@link Call} has been canceled.
	 */
	CANCELED;

	/**
	 * @return <code>true</code> if it is a final state.
	 */
	public boolean isFinal() {
		return this == COMPLETED || this == CANCELED;
	}
}
