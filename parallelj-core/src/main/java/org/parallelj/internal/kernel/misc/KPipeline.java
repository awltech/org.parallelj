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
package org.parallelj.internal.kernel.misc;

import org.parallelj.internal.MessageKind;
import org.parallelj.internal.kernel.KCall;
import org.parallelj.internal.kernel.KCondition;
import org.parallelj.internal.kernel.KElement;
import org.parallelj.internal.kernel.KInputParameter;
import org.parallelj.internal.kernel.KJoin;
import org.parallelj.internal.kernel.KProcedure;
import org.parallelj.internal.kernel.KProcess;
import org.parallelj.internal.kernel.KProgram;
import org.parallelj.internal.kernel.join.KAndJoin;
import org.parallelj.internal.kernel.join.KXorJoin;

/**
 * A KPipeline allows pipelining between {@link KProcedure procedures} of a
 * {@link KProgram}. It insures that at most one instance of an
 * {@link KProcedure} is executing.
 * 
 * It consists in introducing an additional {@link KCondition} between two
 * {@link KProcedure procedures}. For that condition, a token is
 * {@link KCondition#consume(KProcess) consumed} by the {@link KJoin} of the
 * first {@link KProcedure} and a token will be produced by the join of the
 * second procedure.
 * 
 * @author Atos Worldline
 */
public class KPipeline extends KElement {

	/**
	 * The condition that is used to control the enabling of the first
	 * procedure.
	 */
	KCondition condition;

	private final KInputParameter element = new KInputParameter();

	/**
	 * Create a new pipeline.
	 * 
	 * @param program
	 *            the program that contains this pipeline
	 * @param first
	 *            the first procedure of the pipeline
	 * @param last
	 *            the last procedure of the pipeline
	 * 
	 * @param isLastPipeline
	 *            is this pipeline is last
	 * 
	 */
	public KPipeline(KProgram program, KProcedure first, KProcedure last,
			Boolean isLastPipeline) {
		super(program);
		this.condition = new KCondition(program, (short) 1);
		first.setJoin(this.newFirstJoin(first.getJoin()));
		first.addInputParameter(element);
		last.setJoin(this.newLastJoin(last.getJoin()));

		if (isLastPipeline) {
			last.addInputParameter(element);
		}
	}

	protected void iterating(KCall call) {
		element.set(
				call,
				this.getProgram().getPipelineIteratorsMap()
						.get(call.getProcess().getContext()).getNext(call));
	}

	KJoin newFirstJoin(final KJoin join) {
		return new KJoin() {

			@Override
			public boolean isEnabled(KProcess process) {
				return join.isEnabled(process)
						&& KPipeline.this.condition.contains(process);
			}

			@Override
			public void join(KCall call) {
				join.join(call);

				if (join instanceof KXorJoin) {
					KPipeline.this.iterating(call);
				}

				KPipeline.this.condition.consume(call.getProcess());
			}
		};
	}

	KJoin newLastJoin(final KJoin join) {
		return new KJoin() {

			@Override
			public boolean isEnabled(KProcess process) {
				return join.isEnabled(process);
			}

			@Override
			public void join(KCall call) {
				join.join(call);

				if (join instanceof KAndJoin) {
					KPipeline.this.iterating(call);
				}

				KPipeline.this.condition.produce(call.getProcess());
			}
		};
	}

	/**
	 * Helper method that creates a pipeline with a set of {@link KProcedure}.
	 * 
	 * @param program
	 *            the program that contains the pipelines
	 * @param procedures
	 *            the list of procedures to chain in the sampe pipeline
	 */
	public static void pipeline(KProgram program, KProcedure... procedures) {
		if (procedures.length < 2) {
			throw new IllegalArgumentException(MessageKind.E0003.format());
		}

		KProcedure kLastProcedure = procedures[procedures.length - 1];

		KProcedure previous = null;
		boolean isLastPipeline = false;
		for (KProcedure procedure : procedures) {

			if (procedure.equals(kLastProcedure)) {
				isLastPipeline = true;
			}

			if (previous != null) {
				new KPipeline(program, previous, procedure, isLastPipeline);
			}
			previous = procedure;
		}
	}
}
