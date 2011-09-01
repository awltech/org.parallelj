package org.parallelj.mirror;

import java.util.List;

/**
 * Represents a Java Class annotated with {@link org.parallelj.Program}.
 * 
 * @author Laurent Legrand
 * 
 */
public interface ProgramType extends ExecutableType {

	/**
	 * Return the list of procedures declared in the program.
	 * 
	 * @return
	 */
	List<Procedure> getProcedures();
	
	/**
	 * Return the exception handling policy.
	 * 
	 * @return the exception handling policy.
	 */
	ExceptionHandlingPolicy getExceptionHandlingPolicy();

	/**
	 * Create a new {@link Process}
	 * 
	 * @param context
	 *                a context
	 * @return a new process
	 */
	 Process newProcess(Object context);
	 
	 
}
