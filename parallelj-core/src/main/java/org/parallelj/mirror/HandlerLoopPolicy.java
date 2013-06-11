package org.parallelj.mirror;

public enum HandlerLoopPolicy {

	/**
	 * Continue procedure calls in case of Loops.
	 * The handler method will be called each time an Exception is thrown in an loop iteration.
	 * 
	 * Note: As Handler is defined in a Program, it's scope is the Program itself where it is defined.
	 * So if the procedure is linked to a another Program, the HandlerLoopPolicy will have no effect in case of a Loop.  
	 * The Exception handling will have to be managed in the Sub Program itself. 
	 */
	RESUME,
	
	/**
	 * Continue procedure calls in case of Loops.
	 * The handler method will be called each time an Exception is thrown in an loop iteration but only 
	 * when the procedure is completed (all calls for this procedure are terminated).
	 * 
	 * Note: As Handler is defined in a Program, it's scope is the Program itself where it is defined.
	 * So if the procedure is linked to a another Program, the HandlerLoopPolicy will have no effect in case of a Loop.  
	 * The Exception handling will have to be managed in the Sub Program itself. 
	 */
	RESUMEANDWAIT,

	/**
	 * Terminate the current procedure:  don't allow new procedure call in case of Loops.
	 * The handler method is called for each unexpected exception thrown.
	 * 
	 * Note: As Handler is defined in a Program, it's scope is the Program itself where it is defined.
	 * So if the procedure is linked to a another Program, the HandlerLoopPolicy will have no effect in case of a Loop.  
	 * The Exception handling will have to be managed in the Sub Program itself. 
	 */
	TERMINATE,
	
	/**
	 * Terminate the current procedure and wait for already running procedure calls
	 * to complete but don't allow new procedure call in case of Loops.
	 * The handler method is for each unexpected exception thrown.
	 * 
	 * Note: As Handler is defined in a Program, it's scope is the Program itself where it is defined.
	 * So if the procedure is linked to a another Program, the HandlerLoopPolicy will have no effect in case of a Loop.  
	 * The Exception handling will have to be managed in the Sub Program itself. 
	 */
	TERMINATEANDWAIT;
	
	public boolean isWaitingPolicy() {
		return (this == TERMINATEANDWAIT || this == HandlerLoopPolicy.RESUMEANDWAIT);
	}
	
	public boolean isTerminating() {
		return (this == TERMINATEANDWAIT || this == HandlerLoopPolicy.TERMINATE);
	}
	
}
