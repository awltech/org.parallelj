package org.parallelj.mirror;

/**
 * Represents a state change event occurred in a {@link Machine}
 * 
 * @author Laurent Legrand
 * 
 * @param <E>
 */
public interface Event<E extends Enum<E>> {

	/**
	 * Return the machine that has generated this event
	 * 
	 * @param <M>
	 * @return the machine that has generated this event
	 */
	<M extends Machine<E>> M getSource();

	/**
	 * Return the kind of machine that has generated this event
	 * 
	 * @return the kind of machine that has generated this event
	 */
	MachineKind getMachineKind();

	/**
	 * Return the new state
	 * 
	 * @return the new state
	 */
	E getState();

	/**
	 * Return the worker (thread name) that called the state transition
	 * 
	 * @return the worker (thread name) that called the state transition
	 */
	String getWorker();

	/**
	 * Return the timestamp
	 * 
	 * @return the timestamp
	 */
	long getTimestamp();

}
