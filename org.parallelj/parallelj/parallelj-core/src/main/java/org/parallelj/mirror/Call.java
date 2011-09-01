package org.parallelj.mirror;

/**
 * Represents a {@link Procedure} call
 * 
 * @author Laurent Legrand
 * 
 */
public interface Call extends Machine<CallState> {

	/**
	 * @return the procedure which is called
	 */
	public Procedure getProcedure();

}
