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

import java.util.Iterator;

import org.parallelj.internal.kernel.KCondition;
import org.parallelj.internal.kernel.KElement;
import org.parallelj.internal.kernel.KCall;
import org.parallelj.internal.kernel.KInputParameter;
import org.parallelj.internal.kernel.KProcedure;
import org.parallelj.internal.kernel.KJoin;
import org.parallelj.internal.kernel.KProcess;
import org.parallelj.internal.kernel.KSplit;
import org.parallelj.internal.kernel.callback.Iterable;
import org.parallelj.internal.kernel.join.KAbstractJoin;

/**
 * Represents a for each loop.
 * 
 * @author Laurent Legrand
 * 
 */
public class KForEachLoop extends KElement {

	/**
	 * Flag indicating that the iteration has started for this
	 * {@link KForEachLoop}.
	 * 
	 * <p>
	 * If the condition {@link Place#contains(short[])} contains no token
	 * (empty), that means that no action within this {@link KForEachLoop} has
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

	/**
	 * 
	 */
	private Iterable iterable;

	/**
	 * KParameter that will store the element returned by the iterator.
	 */
	private KInputParameter element = new KInputParameter();

	/**
	 * Create a new for each loop.
	 * 
	 * @param first
	 *            the first procedure of the loop
	 * @param last
	 *            the last procedure of the loop
	 */
	public KForEachLoop(final KProcedure first, final KProcedure last) {
		super(first.getProgram());

		if (first == null || last == null) {
			throw new IllegalArgumentException("join or split is null");
		}
		this.started = new KCondition(program);
		this.diff = new KCondition(program);
		first.addInputParameter(this.element);
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
				return hasNext(process)
						&& (started.contains(process) || join
								.isEnabled(process));
			}

			@Override
			public void join(KCall call) {
				if (!started.contains(call.getProcess())) {
					join.join(call);
					started.produce(call.getProcess());
				}
				KForEachLoop.this.element.set(call, KForEachLoop.this.iterable
						.iterator(call.getProcess()).next());
				diff.produce(call.getProcess());

			}
		};
	}

	KSplit newSplit(final KSplit split) {
		return new KSplit() {

			@Override
			public void split(KCall call) {
				diff.consume(call.getProcess());
				if ((diff.isEmpty(call.getProcess()))
						&& !hasNext(call.getProcess())) {
					reset(call.getProcess());
					split.split(call);
				}
			}
		};
	}

	/**
	 * Reset the counters (conditions) for a given process
	 * 
	 * @param process
	 *            the process
	 */
	public void reset(KProcess process) {
		this.started.init(process);
		this.diff.init(process);
		this.iterable.close(process);
	}

	boolean hasNext(KProcess process) {
		Iterator<?> iterator = this.iterable.iterator(process);
		return (iterator == null) ? false : iterator.hasNext();
	}

	/**
	 * @return the iterable this loop iterate on
	 */
	public Iterable getIterable() {
		return iterable;
	}

	/**
	 * Set the iterable this loop iterate on
	 * 
	 * @param iterable
	 *            the iterable
	 */
	public void setIterable(Iterable iterable) {
		this.iterable = iterable;
	}

}
