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