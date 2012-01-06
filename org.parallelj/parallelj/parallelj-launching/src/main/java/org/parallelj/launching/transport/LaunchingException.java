package org.parallelj.launching.transport;


public class LaunchingException extends Exception {

	private String formatedMessage;
	private Exception exception;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7793932389206020914L;

	public LaunchingException(String formatedMessage) {
		this.formatedMessage = formatedMessage;
	}

	public LaunchingException(String formatedMessage, Exception e) {
		this.formatedMessage = formatedMessage;
		this.exception = e;
	}

	public String getFormatedMessage() {
		return formatedMessage;
	}

	public Exception getException() {
		return exception;
	}

}
