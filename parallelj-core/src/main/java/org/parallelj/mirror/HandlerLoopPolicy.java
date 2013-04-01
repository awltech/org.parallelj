package org.parallelj.mirror;

public enum HandlerLoopPolicy {

	/**
	 * Continue procedure calls in case of Loops.
	 * The handler method will be called each time an Exception is thrown in an loop iteration.
	 */
	RESUME,

	/**
	 * Terminate the current procedure: wait for already running procedure calls
	 * to complete but don't allow new procedure call in case of Loops.
	 * The handler method is called only once.
	 * 
	 * Note: As Handler is defined in a Program, it's scope is the Program itself where it is defined.
	 * So if the procedure is linked to a another Program, the HandlerLoopPolicy will have no effect in case of a Loop.  
	 * The Exception handling will have to be managed in the Sub Program itself. 
	 */
	TERMINATE;
	
}
