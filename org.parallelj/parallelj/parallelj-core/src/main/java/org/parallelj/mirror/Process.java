package org.parallelj.mirror;

/**
 * Represents an occurrence of a {@link ProgramType}
 * 
 * @author Laurent Legrand
 * 
 */
public interface Process extends Element, Machine<ProcessState> {

	/**
	 * @return the program type.
	 */
	ProgramType getProgram();

	/**
	 * Return the context bound to the process.
	 * 
	 * @return the context bound to the process.
	 */
	public Object getContext();

	/**
	 * Return the {@link Processor} that executes this process.
	 * 
	 * @return the {@link Processor} that executes this process.
	 *         <code>null</code> if this process has not been started or if it
	 *         is in final state
	 */
	public Processor getProcessor();

	/**
	 * Abort the process.
	 */
	public void abort();

	/**
	 * Terminate the process.
	 */
	public void terminate();

}
