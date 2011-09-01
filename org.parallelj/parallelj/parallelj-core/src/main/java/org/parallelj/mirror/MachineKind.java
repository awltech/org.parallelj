package org.parallelj.mirror;

/**
 * Represents the different kinds of {@link Machine}
 * 
 * @author Laurent Legrand
 * 
 */
public enum MachineKind {

	/**
	 * Correspond to a {@link Processor}
	 */
	PROCESSOR,

	/**
	 * Correspond to a {@link Process}
	 */
	PROCESS,

	/**
	 * Correspond to a {@link Call}
	 */
	CALL;

}
