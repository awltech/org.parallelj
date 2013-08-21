/*
 *     ParallelJ, framework for parallel computing
 *
 *     Copyright (C) 2010, 2011, 2012 Atos Worldline or third-party contributors as
 *     indicated by the @author tags or express copyright attribution
 *     statements applied by the authors.
 *
 *     This library is free software; you can redistribute it and/or
 *     modify it under the terms of the GNU Lesser General Public
 *     License as published by the Free Software Foundation; either
 *     version 2.1 of the License.
 *
 *     This library is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 *     Lesser General Public License for more details.
 *
 *     You should have received a copy of the GNU Lesser General Public
 *     License along with this library; if not, write to the Free Software
 *     Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA
 */
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
			//System.out.println(this.machine);
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
			if (previous != current && current != null) {
				occurrence.getSupport().fireStateChanded(current.value);
			}
			proceed(self);
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
