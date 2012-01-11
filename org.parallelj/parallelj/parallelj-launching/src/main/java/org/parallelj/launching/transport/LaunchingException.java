package org.parallelj.launching.transport;


public class LaunchingException extends Exception {

	private String formatedMessage;
	private Exception exception;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7793932389206020914L;

	public LaunchingException(final String formatedMessage) {
		super(formatedMessage);
		this.formatedMessage = formatedMessage;
	}

	public LaunchingException(final String formatedMessage, final Exception cause) {
		super(formatedMessage, cause);
		this.formatedMessage = formatedMessage;
		this.exception = cause;
	}

	public String getFormatedMessage() {
		return formatedMessage;
	}

	public Exception getException() {
		return exception;
	}

	public void setFormatedMessage(final String formatedMessage) {
		this.formatedMessage = formatedMessage;
	}

	public void setException(final Exception exception) {
		this.exception = exception;
	}

}
