package org.parallelj.mirror;

/**
 * 
 * 
 * @author Laurent Legrand
 * 
 */
public interface Procedure extends NamedElement {

	/**
	 * Return the Java type associated with this procedure
	 * 
	 * @return the Java type associated with this procedure
	 */
	public String getType();

	/**
	 * Return the program that owns this procedure
	 * 
	 * @return the program that owns this procedure
	 */
	public ProgramType getProgram();

}
