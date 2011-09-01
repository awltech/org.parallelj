package org.parallelj.mirror;

/**
 * A processor is responsible to execute a {@link Process process} and all its
 * procedure {@link Call calls}.
 * 
 * @author Atos Worldline
 * 
 */
public interface Processor extends Machine<ProcessorState> {

	/**
	 * Execute a process
	 * 
	 * @param process
	 *            a process to execute
	 */
	public void execute(Process process);

	/**
	 * Suspend the execution of all processes.
	 */
	public void suspend();

	/**
	 * Resume the execution of all processes
	 */
	public void resume();

}
