package org.parallelj.tracknrestart.aspects;

public class TrackNRestartException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public TrackNRestartException() {
	super();
    }

    public TrackNRestartException(String message) {
	super(message);
    }

    public TrackNRestartException(String message, Throwable cause) {
        super(message, cause);
    }

    public TrackNRestartException(Throwable cause) {
        super(cause);
    }

}
