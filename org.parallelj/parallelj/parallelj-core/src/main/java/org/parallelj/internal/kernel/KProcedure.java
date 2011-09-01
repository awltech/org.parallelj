/*
 *     ParallelJ, framework for parallel computing
 *
 *     Copyright (C) 2010 Atos Worldline or third-party contributors as
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
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.parallelj.internal.kernel.callback.Entry;
import org.parallelj.internal.kernel.callback.Exit;
import org.parallelj.mirror.Procedure;

/**
 * TODO javadoc
 * 
 * <div class="note">
 * <p class="title">
 * Implementation note.
 * </p>
 * <p>
 * There is a strong link between {@link KProcedure} and {@link KProgram}.
 * Indeed, {@link KProcedure} could have been developed as an inner class of
 * {@link KProgram}.
 * </p>
 * But doing so, entry a {@link KProcedure} to a new kind of {@link KProgram},
 * which corresponds to a sub class of {@link KProcedure}, would imply a
 * subclass of {@link KProgram}. </p> </div>
 * 
 * @author Atos Worldline
 */
public class KProcedure extends KElement implements Procedure {

	class Marking {

		Set<KCall> calls = new HashSet<KCall>();
	}

	/**
	 * The join of this procedure.
	 */
	KJoin join;

	/**
	 * The split of this procedure.
	 */
	KSplit split;

	/**
	 * 
	 */
	Entry entry = new Entry() {

		@Override
		public void enter(KCall call) {
		}
	};

	/**
	 * 
	 */
	Exit exit = new Exit() {

		@Override
		public void exit(KCall call) {
		}
	};

	/**
	 * Liveness of this procedure: count the number of {@link KCall calls}
	 * currently running.
	 */
	KCondition liveness;

	KHandler handler;

	/**
	 * List of input parameters.
	 */
	List<KInputParameter> inputParameters = new ArrayList<KInputParameter>();

	/**
	 * List of output parameters.
	 */
	List<KOutputParameter> outputParameters = new ArrayList<KOutputParameter>();

	/**
	 * id of the procedure
	 */
	private String id;

	/**
	 * The procedure type.
	 */
	private String type = "<undef>";

	/**
	 * Create a procedure
	 * 
	 * @param program
	 *            the program containing this procedure
	 */
	public KProcedure(KProgram program) {
		super(program);
		this.program.addProcedure(this);
		this.liveness = new KCondition(program, (short) 0, (short) 1);
	}

	@Override
	protected void init(KProcess process) {
		this.setMarking(process, this.newMarking(process));
	}

	protected Marking newMarking(KProcess process) {
		return new Marking();
	}

	@Override
	protected Marking getMarking(KProcess process) {
		return (Marking) super.getMarking(process);
	}

	/**
	 * Verify if this procedure is enabled for a given process.
	 * 
	 * The {@link #liveness} of this procedure and the
	 * {@link KProcess#verifyLiveness() liveness}of the enclosing program are
	 * verified as well as the {@link KJoin#isEnabled(KProcess) enabling} of the
	 * join.
	 * 
	 * @param process
	 *            the process
	 * 
	 * @return <code>true</code> if this procedure is enabled.
	 */
	public final boolean isEnabled(KProcess process) {
		return this.verifyLiveness(process) && this.join.isEnabled(process);
	}

	/**
	 * Verify if new {@link KCall} can be created according to {@link #liveness}
	 * capacity.
	 * 
	 * @param process
	 * @return
	 */
	boolean verifyLiveness(KProcess process) {
		return (this.liveness.size(process) < this.liveness.getCapacity());
	}

	void incrementLiveness(KProcess process) {
		this.program.liveness.produce(process);
		this.liveness.produce(process);
	}

	void decrementLiveness(KProcess process) {
		this.program.liveness.consume(process);
		this.liveness.consume(process);
	}

	final void join(KCall call) {
		this.incrementLiveness(call.getProcess());
		this.join.join(call);
		this.getCalls(call.getProcess()).add(call);
		this.entry.enter(call);
	}

	void onDone(KCall call) {
		if (!this.getCalls(call.getProcess()).remove(call)) {
			return;
		}

		this.decrementLiveness(call.getProcess());
	}

	void split(KCall call) {
		this.exit.exit(call);
		if (this.split != null) {
			this.split.split(call);
		}
	}

	/**
	 * 
	 * @return
	 */
	protected KCall newCall(KProcess process) {
		return new KCall(this, process);
	}

	protected final void terminate(KProcess process) {
		Collection<KCall> collection = new ArrayList<KCall>(
				this.getCalls(process));
		for (KCall call : collection) {
			this.terminate(call);
		}
	}

	protected final void abort(KProcess process) {
		Collection<KCall> collection = new ArrayList<KCall>(
				this.getCalls(process));
		for (KCall call : collection) {
			this.abort(call);
		}

		// this.activity.liveness -= this.steps.size();
		// TODO program.getLiveness().decrement();
		// this.getInstances(program).clear();
	}

	protected void terminate(KCall call) {
	}

	protected void abort(KCall call) {
	}

	/**
	 * Verify if this procedure is busy ({@link KCall} exist) for a given
	 * process
	 * 
	 * @param process
	 *            the given process
	 * @return <code>true</code> if there is at least one active {@link KCall}
	 */
	public boolean isBusy(KProcess process) {
		return this.liveness.contains(process);
	}

	/**
	 * @return the instances
	 */
	protected final Set<KCall> getCalls(KProcess process) {
		return this.getMarking(process).calls;
	}

	/**
	 * @return the join
	 */
	public KJoin getJoin() {
		return this.join;
	}

	/**
	 * @return the split
	 */
	public KSplit getSplit() {
		return this.split;
	}

	/**
	 * @param join
	 *            the join to set
	 */
	public void setJoin(KJoin join) {
		this.join = join;
	}

	/**
	 * @return the capacity of this procedure
	 * @see KCondition#getCapacity()
	 */
	public short getCapacity() {
		return liveness.getCapacity();
	}

	/**
	 * Set the capacity of this procedure
	 * 
	 * @param capacity
	 *            the capacity
	 * @see KCondition#setCapacity(short)
	 */
	public void setCapacity(short capacity) {
		liveness.setCapacity(capacity);
	}

	/**
	 * @param split
	 *            the split to set
	 */
	public void setSplit(KSplit split) {
		this.split = split;
	}

	/**
	 * @return the entry callback
	 */
	public Entry getEntry() {
		return entry;
	}

	/**
	 * set the entry callback
	 * 
	 * @param entry
	 *            the entry callback
	 */
	public void setEntry(Entry entry) {
		this.entry = entry;
	}

	/**
	 * Add an input parameter
	 * 
	 * @param parameter
	 *            the input parameter to add
	 */
	public void addInputParameter(KInputParameter parameter) {
		this.inputParameters.add(parameter);
	}

	/**
	 * Add an output parameter
	 * 
	 * @param parameter
	 *            the output parameter to add
	 */
	public void addOutputParameter(KOutputParameter parameter) {
		this.outputParameters.add(parameter);
	}

	/**
	 * @return the exit callback
	 */
	public Exit getExit() {
		return exit;
	}

	/**
	 * Set the exit callback
	 * 
	 * @param exit
	 *            the exit callback
	 */
	public void setExit(Exit exit) {
		this.exit = exit;
	}

	/**
	 * @return the list of output parameters
	 */
	public List<KOutputParameter> getOutputParameters() {
		return outputParameters;
	}

	public String getId() {
		return id;
	}

	public String getType() {
		return type;
	}

	/**
	 * Set the Java type bound to this procedure
	 * 
	 * @param type
	 *            the Java type
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return the exception handler
	 */
	public KHandler getHandler() {
		return handler;
	}

	/**
	 * Set the exception handler
	 * 
	 * @param handler
	 *            the exception handler
	 */
	public void setHandler(KHandler handler) {
		this.handler = handler;
	}

}