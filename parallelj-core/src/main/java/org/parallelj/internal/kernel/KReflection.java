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
package org.parallelj.internal.kernel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.parallelj.internal.MessageKind;
import org.parallelj.mirror.Event;
import org.parallelj.mirror.EventListener;
import org.parallelj.mirror.ExecutorServiceKind;
import org.parallelj.mirror.Processor;
import org.parallelj.mirror.ProgramType;
import org.parallelj.mirror.Reflection;

/**
 * {@link KReflection} is the default implementation of {@link Reflection}.
 * 
 * @author Laurent Legrand
 * @since 0.5.0
 */
public class KReflection implements Reflection {

	/*
	static class EventConsumer extends Thread {
		
		static class Task extends TimerTask {
			EventConsumer consumer;

			public Task(EventConsumer consumer) {
				this.consumer = consumer;
			}

			double consumerId;

			@Override
			public void run() {
				if (this.consumer != null
						&& this.consumer.getState() == State.WAITING) {
					// The randomId of the consumer is the same as the last check?: the consumer
					// didn't do anything since last check. => it can be stopped.
					if (this.consumerId == this.consumer.getRandomId()) {
						this.consumer.interrupt();
					} else {
						this.consumerId = this.consumer.getRandomId();
					}
				}
			}
		}
		public static long timerPeriod = 500;
		
		BlockingQueue<Event<?>> queue = new ArrayBlockingQueue<Event<?>>(100);

		double randomId;
		
		Timer timer;

		public EventConsumer(BlockingQueue<Event<?>> queue) {
			this.queue = queue;
			this.timer = new Timer();
			this.timer.schedule(new Task(this), 0, timerPeriod);
			this.setName("Consumer");
		}

		@Override
		public void run() {
			try {
				while (true) {
					this.randomId = Math.random();
					Event<?> event;
					event = this.queue.take();
					send(event);
				}
			} catch (InterruptedException e) {
				this.timer.cancel();
			}
		}

		public double getRandomId() {
			return this.randomId;
		}

		private void send(Event<?> event) {
			synchronized (KReflection.Holder.INSTANCE.listeners) {
				for (EventListener listener : KReflection.Holder.INSTANCE.listeners) {
					try {
						listener.handleEvent(event);
					} catch (Exception e) {
						// ignore
						// TODO add message kind
					}
				}
			}
		}
	}

	static class EventManagement extends Thread {
		BlockingQueue<Event<?>> queue = new ArrayBlockingQueue<Event<?>>(5);

		private static class Holder {
			private static final EventManagement INSTANCE = new EventManagement();
		}

		public static EventManagement getInstance() {
			return EventManagement.Holder.INSTANCE;
		}

		EventConsumer consumer;

		Lock lock = new ReentrantLock();

		public synchronized void dispatch(Event<?> event) {
			try {
				lock.lock();
				// Make a consumer available
				checkConsumer();
				synchronized (this.consumer) {
					try {
						checkConsumer();
						this.queue.put(event);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			} finally {
				lock.unlock();
			}
		}

		private void checkConsumer() {
			if (this.consumer == null
					|| (this.consumer != null && this.consumer.getState() == State.TERMINATED)) {
				this.consumer = new EventConsumer(this.queue);
				this.consumer.start();
			}
		}
	}
	*/

	/**
	 * http://en.wikipedia.org/wiki/Initialization_on_demand_holder_idiom
	 * 
	 * @author Laurent Legrand
	 * 
	 */
	private static class Holder {

		private static final KReflection INSTANCE = new KReflection();
	}

	/**
	 * Set of programs
	 */
	Set<KProgram> programs = Collections
			.synchronizedSet(new HashSet<KProgram>());

	/**
	 * Set of event listeners
	 */
	Set<EventListener> listeners = Collections
			.synchronizedSet(new HashSet<EventListener>());

	
	//EventManagement eventManagement = EventManagement.getInstance();
	
	/**
	 * Return the singleton instance.
	 * 
	 * @return the singleton instance.
	 */
	public static final KReflection getInstance() {
		return Holder.INSTANCE;
	}

	private KReflection() {

		// load built-in listener from META-INF
		for (EventListener listener : ServiceLoader.load(EventListener.class)) {
			this.listeners.add(listener);
		}
	}

	@Override
	public List<ProgramType> getPrograms() {
		return new ArrayList<ProgramType>(this.programs);
	}

	void register(KProgram program) {
		if (program != null) {
			this.programs.add(program);
		}
	}

	@Override
	public Processor newProcessor(ExecutorServiceKind executorKind,
			Object... args) {
		switch (executorKind) {
		case NONE:
			return new KProcessor();
		case SINGLE_THREAD_EXECUTOR:
			return new KProcessor(Executors.newSingleThreadExecutor());
		case CACHED_THREAD_POOL:
			return new KProcessor(Executors.newCachedThreadPool());
		case FIXED_THREAD_POOL:
			if ((args.length == 0) || !(args[0] instanceof Integer)) {
				throw new IllegalArgumentException(MessageKind.E0001.format(
						executorKind, (args.length == 0) ? "null" : args[0]));
			}
			return new KProcessor(
					Executors.newFixedThreadPool((Integer) args[0]));
		case PROVIDED:
			if ((args.length == 0) || !(args[0] instanceof ExecutorService)) {
				throw new IllegalArgumentException(MessageKind.E0001.format(
						executorKind, (args.length == 0) ? "null" : args[0]));
			}
			return new KProcessor((ExecutorService) args[0]);
		}
		throw new IllegalArgumentException(MessageKind.E0001.format(
				executorKind, ""));

	}

	/**
	 * Dispatch an event to all listeners.
	 * 
	 * @param event the event to dispatch
	 */
	public void dispatch(Event<?> event) {
		//eventManagement.dispatch(event);
		for (EventListener listener : KReflection.Holder.INSTANCE.listeners) {
			try {
				listener.handleEvent(event);
			} catch (Exception e) {
				// ignore
				// TODO add message kind
			}
		}
	}

	@Override
	public void addEventListener(EventListener listener) {
		if (listener != null) {
			this.listeners.add(listener);
		}
	}

	@Override
	public void removeEventListener(EventListener listener) {
		if (listener != null) {
			this.listeners.remove(listener);
		}
	}

}
