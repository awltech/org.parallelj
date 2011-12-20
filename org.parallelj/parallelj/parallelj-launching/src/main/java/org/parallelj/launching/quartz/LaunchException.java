package org.parallelj.launching.quartz;

import org.quartz.SchedulerException;

public class LaunchException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1368863721559256499L;

	public LaunchException(SchedulerException e) {
		super(e);
	}

	public LaunchException(ClassCastException e) {
		super(e);
	}

}
