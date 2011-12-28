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
 * Represents the possible states of a {@link Process}.
 * 
 * @author Laurent Legrand
 * @since 0.5.0
 */
public enum ProcessState {

	/**
	 * This is the initial state of a {@link Process}.
	 * 
	 * Transition to {@link #RUNNING} when {@link Processor#execute(Process)} is
	 * triggered.
	 * 
	 */
	@Pseudostate(kind = PseudostateKind.INITIAL)
	PENDING,

	/**
	 * The {@link Process} is currently RUNNING meaning that some {@link Call}
	 * are currently RUNNING or new {@link Call} will be launched.
	 * 
	 * <p>
	 * Transition to {@link #COMPLETED} when all {@link Call} are complete.
	 * </p>
	 * 
	 * <p>
	 * Transition to {@link #ABORTING} when {@link Process#abort()} is
	 * triggered.
	 * </p>
	 * 
	 * <p>
	 * Transition to {@link #TERMINATING} when {@link Process#terminate()} is
	 * triggered.
	 * </p>
	 * 
	 */
	RUNNING,

	/**
	 * The {@link Process} is currently ABORTING; waiting for all procedure
	 * {@link Call} to end.
	 * 
	 * <p>
	 * Transition to {@link #ABORTED} when all {@link Call} are done.
	 */
	ABORTING,

	/**
	 * The {@link Process} is currently TERMINATING; waiting for all procedure
	 * {@link Call} to end.
	 * 
	 * <p>
	 * Transition to {@link #TERMINATED} when all {@link Call} are done.
	 */
	TERMINATING,

	/**
	 * This is a final state of the process. No procedures are running and no
	 * new procedures can be launched.
	 */
	ABORTED,

	/**
	 * This is a final state of the process. No procedures are running and no
	 * new procedures can be launched.
	 */
	TERMINATED,

	/**
	 * This is the default final state of the process. The process has completed
	 * its work without interruption (cf. {@link Process#abort()} or
	 * {@link Process#terminate()}).
	 */
	COMPLETED;

	/**
	 * @return <code>true</code> if this state is a final state.
	 */
	public boolean isFinal() {
		return this == COMPLETED || this == ABORTED || this == TERMINATED;
	}

}
