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
package org.parallelj.internal.reflect;

import org.parallelj.Capacity;
import org.parallelj.Pipeline;
import org.parallelj.internal.MessageKind;
import org.parallelj.internal.kernel.KProgram;

/**
 * Build a pipeline based on introspection.
 * 
 * The sequence of building is {@link #start()}, {@link #build()} and
 * {@link #complete()}.
 * 
 * @author a169104
 * 
 */
public class PipelineBuilder extends ProgramBuilder {

	public PipelineBuilder(Class<?> type) {
		super(type);
	}

	public PipelineBuilder build() {
		// init
		MessageKind.I0002.format(getType());
		Pipeline b = getType().getAnnotation(Pipeline.class);
		getProgram().getInputCondition().setName(b.inputCondition());
		getProgram().getOutputCondition().setName(b.outputCondition());
		this.conditions.put(getProgram().getInputCondition().getName(),
				getProgram().getInputCondition());
		this.conditions.put(getProgram().getOutputCondition().getName(),
				getProgram().getOutputCondition());

		// check the capacity
		Capacity capacity = getType().getAnnotation(Capacity.class);
		if (capacity != null && capacity.value() > 0) {
			getProgram().setCapacity(capacity.value());
		}

		getBuilder().build();
		return this;
	}

	public KProgram getPipeline() {
		return getProgram();
	}

}
