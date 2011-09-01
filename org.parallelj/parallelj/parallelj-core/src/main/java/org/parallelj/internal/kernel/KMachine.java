package org.parallelj.internal.kernel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.parallelj.internal.util.sm.Current;
import org.parallelj.mirror.Event;
import org.parallelj.mirror.Machine;
import org.parallelj.mirror.MachineKind;

/**
 * {@link KMachine} is an implementation of a state / transition machine.
 * 
 * <ul>
 * <li>Transitions are represented by {@link KTransition}.</li>
 * <li>States are managed by sub classes of {@link KMachine}.</li>
 * </ul>
 * 
 * At runtime, instances of {@link KTransition} are first enqueued (
 * {@link #firingSequence}) when {@link #submit(KTransition)} is called. Then
 * they are {@link KTransition#fire() fired} if they are
 * {@link KTransition#isEnabled()}.
 * 
 * 
 * Note. Regarding multi-threading aspect.
 * <ul>
 * <li>The firing of {@link KTransition} is done by one thread. Furthermore, a
 * transition needs to be completed before firing a new one.</li>
 * <li>The thread that will fire a transition might not be the thread that
 * enqueued it.</li>
 * <li>But it is ensured that the thread will exit from the
 * {@link #submit(KTransition)} call after the dequeue of the
 * {@link KTransition} that it has submitted.</li>
 * </ul>
 * 
 * @author Laurent Legrand
 * @param <E>
 *            the {@link Enum} that contains the list of states.
 * 
 */
public abstract class KMachine<E extends Enum<E>> implements Machine<E> {

	class KEvent implements Event<E> {

		E state;

		long timestamp = System.currentTimeMillis();

		String worker = Thread.currentThread().getName();

		KEvent(E state) {
			this.state = state;
		}

		@SuppressWarnings("unchecked")
		@Override
		public <M extends Machine<E>> M getSource() {
			return (M) KMachine.this;
		}

		@Override
		public E getState() {
			return this.state;
		}

		@Override
		public String getWorker() {
			return this.worker;
		}

		@Override
		public long getTimestamp() {
			return this.timestamp;
		}

		@Override
		public String toString() {
			return String
					.format("machine=[%s] kind[%s] state=[%s] timestamp=[%d] worker=[%s]",
							KMachine.this, KMachine.this.kind, this.state,
							this.timestamp, this.worker);
		}

		@Override
		public MachineKind getMachineKind() {
			return KMachine.this.kind;
		}

	}

	/**
	 * Current state
	 */
	@Current
	private E state;

	/**
	 * List of events
	 */
	List<Event<E>> events = new ArrayList<Event<E>>();

	KMachine<?> parent;

	MachineKind kind;

	protected KMachine(MachineKind kind, E initial) {
		this.kind = kind;
		this.setState(initial);
	}

	protected KMachine(MachineKind kind, KMachine<?> parent, E initial) {
		this.kind = kind;
		this.parent = parent;
		this.setState(initial);
	}

	protected void setState(E state) {
		if (this.state != state) {
			this.state = state;
			KEvent event = new KEvent(this.state);
			this.events.add(event);
			KReflection.getInstance().dispatch(event);
		}
	}

	@Override
	public synchronized E getState() {
		return this.state;
	}

	@Override
	public List<Event<E>> getEvents() {
		return Collections.unmodifiableList(this.events);
	}

	@Override
	public MachineKind getKind() {
		return this.kind;
	}

}
