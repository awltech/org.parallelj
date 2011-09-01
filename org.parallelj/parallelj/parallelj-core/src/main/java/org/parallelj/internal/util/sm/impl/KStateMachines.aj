package org.parallelj.internal.util.sm.impl;

import javax.xml.bind.annotation.XmlTransient;

import org.aspectj.lang.reflect.MethodSignature;
import org.parallelj.internal.util.sm.StateListener;
import org.parallelj.internal.util.sm.impl.KStateMachine.KVertex;
import org.parallelj.internal.util.sm.impl.KStateMachine.Occurrence;

public privileged aspect KStateMachines {

	/**
	 * 
	 */
	public static interface Adapter {

	}

	/**
	 * Inter type declaration in order to link annotated classes and state
	 * machine occurrence.
	 */
	@XmlTransient
	public Occurrence Adapter.occurrence;

	declare parents:
		(@org.parallelj.internal.util.sm.StateMachine *) implements Adapter;

	public static privileged aspect PerMachine pertypewithin(@org.parallelj.internal.util.sm.StateMachine *) {

		KStateMachine machine;

		after() : staticinitialization(@org.parallelj.internal.util.sm.StateMachine *) {
			this.machine = new KStateMachine(thisJoinPoint.getSignature()
					.getDeclaringType());
			System.out.println(this.machine);
		}

		after(Object self): execution((@org.parallelj.internal.util.sm.StateMachine *).new(..)) && this(self) {
			((Adapter) self).occurrence = this.machine.newOccurrence(self);
		}

		void around(Object self): execution(@org.parallelj.internal.util.sm.Trigger * *(..)) && this(self) {
			Occurrence occurrence = ((Adapter) self).occurrence;
			KVertex previous = occurrence.getCurrent();
			KTrigger trigger = this.machine
					.getTrigger(((MethodSignature) thisJoinPoint.getSignature())
							.getName());
			occurrence.trigger(trigger.trigger(occurrence,
					thisJoinPoint.getArgs()));
			KVertex current = occurrence.getCurrent();
			if (previous != current) {
				occurrence.getSupport().fireStateChanded(current.value);
			}
		}
	}

	void around(Object machine, StateListener listener): execution(* org.parallelj.internal.util.sm.StateMachines.addStateListener(..)) && args(machine, listener, ..) {
		Occurrence occurrence = ((Adapter) machine).occurrence;
		occurrence.getSupport().addStateListener(listener);
	}

	void around(Object machine, StateListener listener): execution(* org.parallelj.internal.util.sm.StateMachines.removeStateListener(..)) && args(machine, listener, ..) {
		Occurrence occurrence = ((Adapter) machine).occurrence;
		occurrence.getSupport().removeStateListener(listener);
	}

}
