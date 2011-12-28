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
 * Represents the possible states of a {@link Processor}.
 * 
 * @author Laurent Legrand
 * @since 0.5.0
 * 
 */
public enum ProcessorState {

	/**
	 * Initial state.
	 * 
	 * Waiting for {@link Process} to be executed.
	 * 
	 * <p>
	 * Transition to {@link #RUNNING} when {@link Processor#execute(Process)} is
	 * called.
	 * </p>
	 * 
	 * <p>
	 * Transition to {@link #SUSPENDED} when {@link Processor#suspend()} is
	 * called.
	 * </p>
	 */
	@Pseudostate(kind = PseudostateKind.INITIAL)
	PENDING,

	/**
	 * The {@link Processor} is active and is executing {@link Process} and
	 * {@link Call}.
	 * 
	 * <p>
	 * Transition to {@link #PENDING} when all
	 * {@link Processor#execute(Process)} are in final states.
	 * </p>
	 * 
	 * <p>
	 * Transition to {@link #SUSPENDED} when {@link Processor#suspend()} is
	 * called.
	 * </p>
	 */
	RUNNING,

	/**
	 * The execution of {@link Process} and {@link Call} is suspended.
	 * 
	 * <p>
	 * Transition to {@link #RUNNING} when {@link Processor#resume()} is called.
	 * </p>
	 * 
	 */
	SUSPENDED;

}