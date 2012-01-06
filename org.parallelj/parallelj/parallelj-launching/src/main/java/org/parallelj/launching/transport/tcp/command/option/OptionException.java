package org.parallelj.launching.transport.tcp.command.option;


public class OptionException extends Exception {

	private String formatedMessage;
	private Throwable exception;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7793932389206020914L;

	public OptionException(String formatedMessage) {
		this.formatedMessage = formatedMessage;
	}

	public OptionException(String formatedMessage, Throwable e) {
		this.formatedMessage = formatedMessage;
		this.exception = e;
	}

	public String getFormatedMessage() {
		return formatedMessage;
	}

	public Throwable getException() {
		return exception;
	}

}
