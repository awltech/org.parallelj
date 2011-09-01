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
