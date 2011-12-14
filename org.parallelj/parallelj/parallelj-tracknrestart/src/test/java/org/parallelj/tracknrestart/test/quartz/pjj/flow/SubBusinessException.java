package org.parallelj.tracknrestart.test.quartz.pjj.flow;

public class SubBusinessException extends Exception {

	private static final long serialVersionUID = 1L;

	public SubBusinessException() {
	super();
    }

    public SubBusinessException(String message) {
	super(message);
    }

    public SubBusinessException(String message, Throwable cause) {
        super(message, cause);
    }

    public SubBusinessException(Throwable cause) {
        super(cause);
    }

}
