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

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicLong;

import org.parallelj.internal.util.sm.Entry;
import org.parallelj.internal.util.sm.StateMachine;
import org.parallelj.internal.util.sm.Transition;
import org.parallelj.internal.util.sm.TransitionKind;
import org.parallelj.internal.util.sm.Transitions;
import org.parallelj.internal.util.sm.Trigger;
import org.parallelj.mirror.MachineKind;
import org.parallelj.mirror.ProcessState;
import org.parallelj.mirror.Processor;
import org.parallelj.mirror.ProcessorState;

/**
 * A processor is responsible to execute a {@link KProcess process} and all its
 * procedure {@link KCall calls}.
 * 
 * @author Atos Worldline
 * 
 */
@StateMachine(states = ProcessorState.class, transitions = {
		@Transition(source = "PENDING", target = "SUSPENDED", triggers = "suspend"),
		@Transition(source = "RUNNING", target = "SUSPENDED", triggers = "suspend"),
		@Transition(source = "RUNNING", target = "PENDING", triggers = "complete", guard = "isEmpty") })
public class KProcessor extends KMachine<ProcessorState> implements Processor {

	/**
	 * stores the current processor; if any.
	 * 
	 */
	private static ThreadLocal<KProcessor> current = new ThreadLocal<KProcessor>();

	/**
	 * Sequence for processor ids.
	 */
	private static AtomicLong sequence = new AtomicLong(1);

	/**
	 * Identifier of this process.
	 */
	private String id = "" + sequence.getAndIncrement();

	/**
	 * Executor that will execute the runnable
	 */
	private Executor executor;

	/**
	 * Set of active processes
	 */
	Set<KProcess> processes = new HashSet<KProcess>();

	/**
	 * Create a {@link KProcessor} with a default executor service.
	 */
	public KProcessor() {
		this(null);
	}

	/**
	 * Create a {@link Processor} with a given executor service
	 * 
	 * @param executor
	 *            the executor service
	 */
	public KProcessor(ExecutorService executor) {
		super(MachineKind.PROCESSOR, ProcessorState.PENDING);

		// if the executor is null, create a dummy one that just run the command
		this.executor = (executor != null) ? executor : new Executor() {

			@Override
			public void execute(Runnable command) {
				command.run();
			}
		};
	}

	/**
	 * Execute a process.
	 * 
	 * @param process
	 *            the process to execute
	 */
	public void execute(org.parallelj.mirror.Process process) {

		if (!(process instanceof KProcess)) {
			return;
		}
		this.execute((KProcess) process);
	}

	/**
	 * Execute a {@link KProcess}
	 * 
	 * @param process
	 *            the process to execute
	 */
	@Trigger
	public void execute(final KProcess process) {
	}

	@Transitions({
			@Transition(source = "PENDING", target = "RUNNING", triggers = "execute"),
			@Transition(source = "RUNNING", target = "RUNNING", triggers = "execute", kind = TransitionKind.LOCAL) })
	void doExecute(KProcess process) {
		process.setProcessor(KProcessor.this);
		KProcessor.this.processes.add(process);
		KProcessor.this.fire(process);
	}

	@Trigger
	public void suspend() {
	}

	@Trigger
	public void resume() {
	}

	@Transition(source = "SUSPENDED", target = "RUNNING", triggers = "resume")
	void doResume() {
		if (KProcessor.this.processes.isEmpty()) {
			KProcessor.this.complete();
		} else {
			for (KProcess process : KProcessor.this.processes) {
				KProcessor.this.fire(process);
			}
		}
	}

	@Trigger
	void fire(final KProcess process) {
	}

	@Trigger
	void complete() {
	}

	@Transition(source = "RUNNING", target = "RUNNING", triggers = "fire")
	void doFire(KProcess process) {
		// start the process if not already started
		if (process.getState() == ProcessState.PENDING) {
			process.start();
		}

		// process is ended ?
		if (process.getState().isFinal()) {
			this.processes.remove(process);
			if (this.processes.size() == 0) {
				this.complete();
			}
			return;
		}
		// submit all procedure calls
		Set<KCall> calls = new HashSet<KCall>();
		process.fire(calls);
		for (KCall call : calls) {
			// Next line removed: The event is only catch by the KProcess
			//StateMachines.addStateListener(call, this);
			KProcessor.this.submit(call.toRunnable());
		}
	}

/* MOVED to KProcess.stateChanged(..) 
	@Override
	public void stateChanged(StateEvent<KCall, CallState> event) {
		if (event.getState().isFinal()) {
			StateMachines.removeStateListener(event.getSource(), this);
			this.fire(event.getSource().getProcess());
		}
	}
*/
	
	class KProcessorRunnable implements Runnable {
		private Runnable runnable;
		
		public KProcessorRunnable(Runnable runnable) {
			this.runnable = runnable;
		}

		@Override
		public void run() {
			try {
				// install this processor in the thread local
				KProcessor.current.set(KProcessor.this);
				runnable.run();
			} finally {
				// uninstall this processor in the thread local
				KProcessor.current.set(null);
			}
		}
		
	}
	
	/**
	 * Submit a runnable to be executed by the executor
	 * 
	 * @param runnable
	 *            the runnable to execute
	 */
	public void submit(final Runnable runnable) {
		this.executor.execute(new KProcessorRunnable(runnable));
	}

	@Entry({ "RUNNING", "PENDING", "SUSPENDED" })
	void entering() {
	}

	/**
	 * @return the current processor associated to the current thread
	 */
	public static KProcessor currentProcessor() {
		return current.get();
	}

	public String getId() {
		return this.id;
	}

	/**
	 * @return <code>true</code> if there is no more process to execute
	 */
	public boolean isEmpty() {
		return processes.isEmpty();
	}

}
