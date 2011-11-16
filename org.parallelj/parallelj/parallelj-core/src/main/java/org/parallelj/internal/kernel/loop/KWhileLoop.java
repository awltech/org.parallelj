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

package org.parallelj.internal.kernel.loop;

import org.parallelj.internal.kernel.KCall;
import org.parallelj.internal.kernel.KCondition;
import org.parallelj.internal.kernel.KElement;
import org.parallelj.internal.kernel.KJoin;
import org.parallelj.internal.kernel.KProcedure;
import org.parallelj.internal.kernel.KProcess;
import org.parallelj.internal.kernel.KSplit;
import org.parallelj.internal.kernel.callback.Predicate;
import org.parallelj.internal.kernel.join.KAbstractJoin;

/**
 * Represents a while loop
 * 
 * @author Laurent Legrand
 * 
 */
public class KWhileLoop extends KElement {

	/**
	 * Flag indicating that the iteration has started for this
	 * {@link KWhileLoop} .
	 * 
	 * <p>
	 * If the condition {@link Place#contains(short[])} contains no token
	 * (empty), that means that no action within this {@link KWhileLoop} has
	 * been started.
	 * </p>
	 * <p>
	 * If the condition contains tokens, the iteration has been started.
	 * </p>
	 * 
	 */
	private KCondition started;

	/**
	 * Corresponds to the difference between the number of tokens consumed by
	 * the first procedure and the number of tokens produced by the last one.
	 */
	private KCondition diff;

	private Predicate predicate;

	/**
	 * Create a new while loop
	 * 
	 * @param first
	 *            the first procedure of the loop
	 * @param last
	 *            the last procedure of the loop
	 */
	public KWhileLoop(final KProcedure first, final KProcedure last) {
		super(first.getProgram());

		if (first == null || last == null) {
			throw new IllegalArgumentException("join or split is null");
		}
		this.started = new KCondition(program);
		this.diff = new KCondition(program);
		first.setJoin(this.newJoin(first.getJoin()));
		last.setSplit(this.newSplit(last.getSplit()));

	}

	/**
	 * Return a {@link KAbstractJoin} that encapsulated the join passed as
	 * parameter.
	 * 
	 * @param join
	 * @return
	 */
	KJoin newJoin(final KJoin join) {
		return new KJoin() {

			@Override
			public boolean isEnabled(KProcess process) {
				return predicate.verify(process)
						&& (started.contains(process) || join
								.isEnabled(process));
			}

			@Override
			public void join(KCall call) {
				if (!started.contains(call.getProcess())) {
					join.join(call);
					started.produce(call.getProcess());
				}
				diff.produce(call.getProcess());
				KWhileLoop.this.iterating(call);
			}
		};
	}

	KSplit newSplit(final KSplit split) {
		return new KSplit() {

			@Override
			public void split(KCall call) {
				diff.consume(call.getProcess());
				if ((diff.isEmpty(call.getProcess()))
						&& !predicate.verify(call.getProcess())) {
					complete(split, call);
				}
			}
		};
	}
	
	/**
	 * Called at the end of a KWhileLoop
	 * 
	 * @param split
	 * @param call
	 * @return void
	 */
	private void complete(KSplit split, KCall call) {
		reset(call.getProcess());
		split.split(call);
	}

	/**
	 * Reset the counters for a given process
	 * 
	 * @param process
	 *            the process
	 */
	public void reset(KProcess process) {
		this.started.init(process);
		this.diff.init(process);
	}

	/**
	 * @return the predicate
	 */
	public Predicate getPredicate() {
		return predicate;
	}

	/**
	 * Set the predicate
	 * 
	 * @param predicate
	 *            the predicate
	 */
	public void setPredicate(Predicate predicate) {
		this.predicate = predicate;
	}

	/**
	 * Method called each time there is a new iteration.
	 * 
	 * @param call
	 */
	protected void iterating(KCall call) {

	}

}
