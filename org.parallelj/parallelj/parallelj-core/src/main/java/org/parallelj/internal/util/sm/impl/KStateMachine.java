package org.parallelj.internal.util.sm.impl;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.ReentrantLock;

import org.parallelj.internal.util.Classes;
import org.parallelj.internal.util.sm.Current;
import org.parallelj.internal.util.sm.Entry;
import org.parallelj.internal.util.sm.Exit;
import org.parallelj.internal.util.sm.Pseudostate;
import org.parallelj.internal.util.sm.PseudostateKind;
import org.parallelj.internal.util.sm.StateChangeSupport;
import org.parallelj.internal.util.sm.StateMachine;
import org.parallelj.internal.util.sm.Transition;
import org.parallelj.internal.util.sm.TransitionKind;
import org.parallelj.internal.util.sm.Transitions;
import org.parallelj.internal.util.sm.Trigger;

public class KStateMachine {

	class Occurrence {

		StateChangeSupport support;

		Object self;

		Occurrence(Object self) {
			this.self = self;
			this.support = new StateChangeSupport(this.self);
			this.setCurrent(KStateMachine.this.initial);
		}

		KVertex current;

		private Queue<KCallEvent> eventPool = new ConcurrentLinkedQueue<KCallEvent>();

		/**
		 * Lock used to protect methods that triggers a transition.
		 */
		private ReentrantLock lock = new ReentrantLock();

		public void trigger(KCallEvent event) {
			this.eventPool.add(event);
			this.dispatch();
		}

		private void dispatch() {
			// check if the current thread is already firing a transition.
			if (this.lock.isHeldByCurrentThread()) {
				return;
			}

			this.lock.lock();
			try {
				KCallEvent event;
				while ((event = this.eventPool.poll()) != null) {
					KTransition transition = KStateMachine.this.getTransition(
							event.trigger, this.getCurrent());
					if (transition != null && transition.isEnabled(event)) {
						synchronized (this.self) {
							if (transition.kind != TransitionKind.LOCAL) {
								this.exiting();
							}
							transition.fire(event);
							if (transition.kind != TransitionKind.LOCAL) {
								this.entering(transition.getTarget());
							}
						}
					} else {
						// TODO add message kind
					}
				}
			} finally {
				this.lock.unlock();
			}

		}

		KVertex getCurrent() {
			return current;
		}

		void exiting() {
			if (this.current == null) {
				return;
			}
			if (this.current instanceof KState) {
				((KState) this.current).exiting(this);
			}
			this.setCurrent(null);
		}

		void entering(KVertex vertex) {
			this.setCurrent(vertex);
			if (this.current instanceof KState) {
				((KState) this.current).entering(this);
			}
		}

		void setCurrent(KVertex current) {
			this.current = current;
			if (KStateMachine.this.current != null) {
				try {
					KStateMachine.this.current.set(this.self,
							(current != null) ? current.value : null);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		public StateChangeSupport getSupport() {
			return support;
		}

	}

	class KTransition {

		TransitionKind kind;

		List<KTrigger> triggers = new ArrayList<KTrigger>();

		KVertex source;

		KVertex target;

		Method guard;

		Method effect;

		boolean isEnabled(KCallEvent event) {
			try {
				return (this.guard == null) ? true : (Boolean) this.guard
						.invoke(((Occurrence) event.getSource()).self);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return true;
		}

		void fire(KCallEvent event) {
			try {
				if (this.effect != null) {
					this.effect.invoke(((Occurrence) event.getSource()).self,
							event.args);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		List<KTrigger> getTriggers() {
			return triggers;
		}

		KVertex getSource() {
			return source;
		}

		KVertex getTarget() {
			return target;
		}

		@Override
		public String toString() {
			return "KTransition [triggers=" + triggers + ", source=" + source
					+ ", target=" + target + ", guard=" + guard + ", effect="
					+ effect + "]";
		}
	}

	class KFinalState extends KState {

		public KFinalState(Enum<?> value) {
			super(value);
		}

	}

	class KState extends KVertex {

		Method entry;

		Method exit;

		public KState(Enum<?> value) {
			super(value);
		}

		void entering(Occurrence occurrence) {
			try {
				if (this.entry != null) {
					this.entry.invoke(occurrence.self);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		void exiting(Occurrence occurrence) {
			try {
				if (this.exit != null) {
					this.exit.invoke(occurrence.self);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	class KPseudostate extends KVertex {

		public KPseudostate(Enum<?> value) {
			super(value);
		}

	}

	abstract class KVertex {

		String name;

		Enum<?> value;

		public KVertex(Enum<?> value) {
			this.value = value;
			this.name = value.name();
		}

		@Override
		public String toString() {
			return "KVertex [name=" + name + ", value=" + value + "]";
		}

	}

	KPseudostate initial;

	Class<?> type;

	Field current;

	Map<KTrigger, Map<KVertex, KTransition>> transitions = new HashMap<KTrigger, Map<KVertex, KTransition>>();

	Map<String, KVertex> states = new HashMap<String, KStateMachine.KVertex>();

	Map<String, KTrigger> triggers = new HashMap<String, KTrigger>();

	public KStateMachine(Class<?> type) {
		this.type = type;
		try {
			this.build();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void build() throws Exception {
		StateMachine machine = type.getAnnotation(StateMachine.class);
		for (Enum<?> e : machine.states().getEnumConstants()) {
			Pseudostate pseudostate = e.getDeclaringClass().getField(e.name())
					.getAnnotation(Pseudostate.class);
			if (pseudostate != null) {
				KPseudostate p = new KPseudostate(e);
				this.states.put(e.toString(), p);
				if (pseudostate.kind() == PseudostateKind.INITIAL) {
					this.initial = p;
				}
			} else {
				this.states.put(e.toString(), new KState(e));
			}
		}

		List<Field> fields = Classes.findFields(type, Current.class);
		if (!fields.isEmpty()) {
			this.current = fields.get(0);
			this.current.setAccessible(true);
		}

		for (Method method : Classes.findMethods(type, Trigger.class)) {
			this.triggers.put(method.getName(), new KTrigger(method.getName()));
		}

		for (Transition transition : machine.transitions()) {
			this.addTransition(null, transition);
		}

		for (Method method : Classes.findMethods(type, Transition.class)) {
			this.addTransition(method, method.getAnnotation(Transition.class));
			method.setAccessible(true);
		}
		for (Method method : Classes.findMethods(type, Transitions.class)) {
			method.setAccessible(true);
			for (Transition transition : method
					.getAnnotation(Transitions.class).value()) {
				this.addTransition(method, transition);
			}
		}
		for (Method method : Classes.findMethods(type, Entry.class)) {
			method.setAccessible(true);
			for (String name : method.getAnnotation(Entry.class).value()) {
				KVertex vertex = this.getState(name);
				if (vertex != null && vertex instanceof KState) {
					((KState) vertex).entry = method;
				}
			}
		}
		for (Method method : Classes.findMethods(type, Exit.class)) {
			method.setAccessible(true);
			for (String name : method.getAnnotation(Exit.class).value()) {
				KVertex vertex = this.getState(name);
				if (vertex != null && vertex instanceof KState) {
					((KState) vertex).exit = method;
				}
			}
		}
	}

	private void addTransition(Method method, Transition transition) {
		KTransition t = new KTransition();
		t.kind = transition.kind();
		t.source = this.getState(transition.source());
		t.target = this.getState(transition.target());
		t.effect = method;
		for (String s : transition.triggers()) {
			t.triggers.add(this.getTrigger(s));
		}
		if (!transition.guard().isEmpty()) {
			t.guard = Classes.findMethod(this.type, transition.guard());
		}
		for (KTrigger trigger : t.triggers) {
			Map<KVertex, KTransition> map = this.transitions.get(trigger);
			if (map == null) {
				map = new HashMap<KVertex, KTransition>();
				this.transitions.put(trigger, map);
			}
			map.put(t.source, t);
		}
	}

	Occurrence newOccurrence(Object self) {
		return new Occurrence(self);
	}

	KTransition getTransition(KTrigger trigger, KVertex vertex) {
		Map<KVertex, KTransition> map = this.transitions.get(trigger);
		return (map == null) ? null : map.get(vertex);
	}

	KVertex getState(String name) {
		return this.states.get(name);
	}

	KTrigger getTrigger(String name) {
		return this.triggers.get(name);
	}

	@Override
	public String toString() {
		return "KStateMachine [type=" + type + ", transitions=" + transitions
				+ ", states=" + states + ", triggers=" + triggers + "]";
	}
}
