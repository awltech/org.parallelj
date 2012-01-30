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

import java.util.Set;

import org.parallelj.internal.util.sm.Entry;
import org.parallelj.internal.util.sm.StateEvent;
import org.parallelj.internal.util.sm.StateListener;
import org.parallelj.internal.util.sm.StateMachine;
import org.parallelj.internal.util.sm.StateMachines;
import org.parallelj.internal.util.sm.Transition;
import org.parallelj.internal.util.sm.Transitions;
import org.parallelj.internal.util.sm.Trigger;
import org.parallelj.mirror.CallState;
import org.parallelj.mirror.MachineKind;
import org.parallelj.mirror.Process;
import org.parallelj.mirror.ProcessState;

/**
 * Represents an instance of a {@link KProgram}.
 * 
 * It cannot be instantiated directly; {@link KProgram#newProcess(Object)} must
 * be used instead.
 * 
 * @author Atos Worldline
 */
@StateMachine(states = ProcessState.class, transitions = {
		@Transition(source = "RUNNING", target = "TERMINATING", triggers = "terminate"),
		@Transition(source = "RUNNING", target = "ABORTING", triggers = "abort") })
public class KProcess extends KMachine<ProcessState> implements Process, StateListener<KCall, CallState> {

	/**
	 * 
	 */
	private final KProgram program;

	/**
	 * KDefinition level context
	 */
	final Object context;

	/**
	 * Marking of all elements.
	 */
	Object[] markings;

	/**
	 * id of the process
	 */
	private String id;

	/**
	 * The parent id.
	 */
	private String parentId = "0.0.0.0";

	KProcessor processor;

	protected KProcess(KProgram program, Object context) {
		super(MachineKind.PROCESS, ProcessState.PENDING);
		this.program = program;
		this.context = context;
	}

	boolean isEnabled() {
		return this.verify() && this.verifyLiveness();
	}

	boolean verifyLiveness() {
		return (this.program.liveness.size(this) < this.program.liveness
				.getCapacity());
	}

	void incrementLiveness() {
		this.program.liveness.produce(this);
	}

	void decrementLiveness() {
		this.program.liveness.consume(this);
	}

	boolean isBusy() {
		return this.program.liveness.size(this) != 0;
	}
	
	boolean isAlive() {
		if (this.isBusy()) {
			return true;
		}
		for (KProcedure procedure : this.program.procedures) {
			if (procedure.isEnabled(this)) {
				// at least one procedure is enabled
				return true;
			}
		}
		return false;
	}

	/**
	 * Verify a pre condition which is global to the process. It will be called
	 * prior to try firing a execution ( {@link KProcedure#tryFiring(KProcess)}.
	 * 
	 * @return <code>true</code>if process is able to fire {@link KProcedure}.
	 */
	public boolean verify() {
		return true;
	}

	/**
	 * Start this process.
	 */
	@Trigger
	public final void start() {

	}

	@Transition(source = "PENDING", target = "RUNNING", triggers = "start")
	void doStart() {
		for (KProcedure procedure : this.program.procedures) {
			if (procedure.isEnabled(this)) {
				// at least one procedure is enabled
				return;
			}
		}

		// no procedure enabled, => completed
		this.complete();
	}

	@Trigger
	void handleCompletedCall(final KCall call) {
	}

	@Transition(source = "RUNNING", target = "RUNNING", triggers = "handleCompletedCall")
	void doHandleCompletedCallWhileRunning(KCall call) {
		KProcedure procedure = call.getProcedure();
		procedure.onDone(call);
		if (call.getException() == null) {
			procedure.split(call);
		} else {
			if (procedure.getHandler() != null) {
				procedure.getHandler().addCallOnError(call);
			} else {
				switch (this.program.getExceptionHandlingPolicy()) {
				case RESUME:
					procedure.split(call);
					break;
				case TERMINATE:
					this.terminate();
					break;
				case ABORT:
					this.abort();
					break;
				}
			}
		}
		// check completeness
		if (!this.isAlive()) {
			this.complete();
		}
	}

	@Transitions({
			@Transition(source = "ABORTING", target = "ABORTING", triggers = "handleCompletedCall"),
			@Transition(source = "TERMINATING", target = "TERMINATING", triggers = "handleCompletedCall") })
	void doHandleCompletedCall(KCall call) {
		call.getProcedure().onDone(call);
		// check completeness
		if (!this.isBusy()) {
			this.complete();
		}
	}

	/**
	 * Fire the enable procedures.
	 * 
	 * @return the procedure calls that are ready to be executed.
	 */
	@Trigger
	public final void fire(final Set<KCall> calls) {
	}

	@Transition(source = "RUNNING", target = "RUNNING", triggers = "fire")
	void doFire(Set<KCall> calls) {
		for (KProcedure procedure : this.program.procedures) {
			// check pre condition
			if (!this.isEnabled()) {
				break;
			}
			while (this.isEnabled() && procedure.isEnabled(KProcess.this)) {
				KCall call = procedure.newCall(KProcess.this);
				StateMachines.addStateListener(call, this);
				// TODO refactor: should be
				// included in newCall
				procedure.join(call);
				calls.add(call);
			}
		}

		// check completeness
		if (!this.isBusy()) {
			this.complete();
		}
	}

	@Trigger
	private void complete() {
	}

	@Transitions({
			@Transition(source = "RUNNING", target = "COMPLETED", triggers = "complete"),
			@Transition(source = "ABORTING", target = "ABORTED", triggers = "complete"),
			@Transition(source = "TERMINATING", target = "TERMINATED", triggers = "complete") })
	void doComplete() {
	}

	@Trigger
	public final void terminate() {
	}

	@Trigger
	public final void abort() {
	}

	@Entry({ "COMPLETED", "ABORTED", "TERMINATED" })
	void done() {
	}

	public Object getContext() {
		return context;
	}

	public String getId() {
		return id;
	}

	public KProgram getProgram() {
		return program;
	}

	/**
	 * @return the parent id if any.
	 */
	public String getParentId() {
		return parentId;
	}

	/**
	 * Set the parent id
	 * 
	 * @param parentId
	 *            the parent id
	 */
	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public KProcessor getProcessor() {
		return processor;
	}

	void setProcessor(KProcessor processor) {
		this.processor = processor;
	}

	@Override
	public void stateChanged(StateEvent<KCall, CallState> event) {
		switch (event.getState()) {
		case COMPLETED:
			StateMachines.removeStateListener(event.getSource(), this);
			this.handleCompletedCall(event.getSource());
		}
	}
}