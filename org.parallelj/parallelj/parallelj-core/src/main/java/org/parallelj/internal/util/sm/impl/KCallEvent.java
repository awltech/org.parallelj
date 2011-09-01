package org.parallelj.internal.util.sm.impl;

import java.util.EventObject;

public class KCallEvent extends EventObject {

	final KTrigger trigger;

	final Object[] args;

	public KCallEvent(Object source, KTrigger trigger, Object[] args) {
		super(source);
		this.trigger = trigger;
		this.args = args;
	}

}
