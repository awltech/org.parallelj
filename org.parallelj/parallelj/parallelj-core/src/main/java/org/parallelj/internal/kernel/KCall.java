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

import org.parallelj.internal.util.sm.Entry;
import org.parallelj.internal.util.sm.StateMachine;
import org.parallelj.internal.util.sm.Transition;
import org.parallelj.internal.util.sm.Trigger;
import org.parallelj.mirror.Call;
import org.parallelj.mirror.CallState;
import org.parallelj.mirror.MachineKind;

/**
 * A call is an instance of an {@link KProcedure}.
 * 
 * <h1>Subclasses contract</h1>
 * <p>
 * Subclasses must inform when they {@link #start() start} and when they
 * {@link #complete() complete}.
 * 
 * @see KProcedure#newCall(KProcess)
 * 
 * @author Atos Worldline
 */
@StateMachine(states = CallState.class, transitions = {
		@Transition(source = "PENDING", target = "RUNNING", triggers = "start"),
		@Transition(source = "PENDING", target = "CANCELED", triggers = "cancel"),
		@Transition(source = "RUNNING", target = "COMPLETED", triggers = "complete")
		})
public class KCall extends KMachine<CallState> implements Call {

	/**
	 * The procedure that created this instance.
	 */
	private final KProcedure procedure;

	/**
	 * The process that owns this instance.
	 */
	private final KProcess process;

	/**
	 * KTask level context.
	 * 
	 */
	Object context;

	/**
	 * The values of the {@link KTask#inputParameters}.
	 */
	Object[] inputValues;

	/**
	 * The values of the {@link KTask#outputParameters}.
	 */
	Object[] outputValues;

	Exception exception;

	/**
	 * id of the procedure call
	 */
	private String id;

	protected KCall(KProcedure procedure, KProcess process) {
		super(MachineKind.CALL, process, CallState.PENDING);
		this.procedure = procedure;
		this.process = process;
		this.inputValues = new Object[this.procedure.inputParameters.size()];
		this.outputValues = new Object[this.procedure.outputParameters.size()];
	}

	/**
	 * Start the call.
	 */
	@Trigger
	public void start() {
	}

	/**
	 * Complete the call.
	 */
	@Trigger
	public void complete() {
	}
	
	@Entry("RUNNING")
	void onRunning() { }

	@Entry("COMPLETED")
	void onComplete() {
		//this.getProcess().handleCompletedCall(this);
	}
	
	@Entry("CANCELED")
	void onCanceled() {
		//this.getProcess().handleCompletedCall(this);
	}

	/**
	 * Cancel the call.
	 */
	@Trigger
	public void cancel() {
	}

	/**
	 * Convert the call to a {@link Runnable}.
	 * 
	 * @return a runnable.
	 */
	public Runnable toRunnable() {
		return new Runnable() {
			@Override
			public void run() {
				KCall.this.start();
				KCall.this.complete();
			}
		};
	}

	/**
	 * Get the context bound to this call.
	 * 
	 * @return the context bound to this call
	 */
	public Object getContext() {
		return context;
	}

	/**
	 * Set the context bound to this call
	 * 
	 * @param context
	 *            the context
	 */
	public void setContext(Object context) {
		this.context = context;
	}

	/**
	 * @return the process
	 */
	public KProcess getProcess() {
		return process;
	}

	/**
	 * 
	 * @return the input values
	 */
	public Object[] getInputValues() {
		return inputValues;
	}

	/**
	 * @return the output values
	 */
	public Object[] getOutputValues() {
		return outputValues;
	}

	/**
	 * 
	 * @return the exception if any
	 */
	public Exception getException() {
		return exception;
	}

	protected void setException(Exception exception) {
		this.exception = exception;
	}

	public String getId() {
		return id;
	}

	public KProcedure getProcedure() {
		return procedure;
	}

}