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
package org.parallelj.internal.kernel.loop;

import org.parallelj.internal.kernel.KCall;
import org.parallelj.internal.kernel.KElement;
import org.parallelj.internal.kernel.KInputParameter;
import org.parallelj.internal.kernel.KJoin;
import org.parallelj.internal.kernel.KProcedure;
import org.parallelj.internal.kernel.KProcess;
import org.parallelj.internal.kernel.KSplit;
import org.parallelj.internal.kernel.callback.Iterable;
import org.parallelj.internal.kernel.join.KAbstractJoin;
import org.parallelj.internal.reflect.callback.FieldIterable;

/**
 * @author a169104
 */
public class KPipelineForEachLoop extends KElement {
	/**
	 * 
	 */
	private Iterable iterable;

	/**
	 * KParameter that will store the element returned by the iterator.
	 */
	private final KInputParameter element = new KInputParameter();

	/**
	 * Create a new for each loop for pipeline.
	 * 
	 * @param kProcedure
	 *            the first procedure from which pipeline call will be made
	 * 
	 */
	public KPipelineForEachLoop(final KProcedure kProcedure) {
		super(kProcedure.getProgram());
		kProcedure.addInputParameter(element);

		kProcedure.setJoin(this.newJoin(kProcedure.getJoin()));
		kProcedure.setSplit(this.newSplit(kProcedure.getSplit()));
	}

	protected void iterable(KCall call) {
		element.set(call,
				((FieldIterable) iterable).getIterable(call.getProcess()));
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
				return join.isEnabled(process);
			}

			@Override
			public void join(KCall call) {
				join.join(call);
				KPipelineForEachLoop.this.iterable(call);
			}
		};
	}

	KSplit newSplit(final KSplit split) {
		return new KSplit() {

			@Override
			public void split(KCall call) {
				split.split(call);
			}
		};
	}

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
