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
