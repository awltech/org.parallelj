package org.parallelj.mirror;

import java.util.List;

/**
 * Represents a state machine.
 * 
 * @author Laurent Legrand
 * 
 * @param <E>
 *                the enum that holds all possible states.
 */
public interface Machine<E extends Enum<E>> extends Element {
	
	/**
	 * Return the kind of machine
	 * 
	 * @return
	 */
	public MachineKind getKind();

	/**
	 * Return the current state
	 * 
	 * @return
	 */
	public E getState();

	/**
	 * Return the list of state change events.
	 * 
	 * @return the list of state change events.
	 */
	List<Event<E>> getEvents();

}
