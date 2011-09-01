package org.parallelj.internal.util.sm.impl;

import org.parallelj.internal.util.sm.impl.KStateMachine.Occurrence;

public class KTrigger {

	String name;

	public KTrigger(String name) {
		super();
		this.name = name;
	}

	public KCallEvent trigger(Occurrence occurrence, Object[] args) {
		return new KCallEvent(occurrence, this, args);
	}

	@Override
	public String toString() {
		return "KTrigger [name=" + name + "]";
	}

}
