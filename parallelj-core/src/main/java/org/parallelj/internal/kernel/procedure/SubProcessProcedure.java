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
package org.parallelj.internal.kernel.procedure;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Iterator;

import org.parallelj.internal.MessageKind;
import org.parallelj.internal.kernel.KCall;
import org.parallelj.internal.kernel.KProcedure;
import org.parallelj.internal.kernel.KProcess;
import org.parallelj.internal.kernel.KProcessor;
import org.parallelj.internal.kernel.KProgram;
import org.parallelj.internal.reflect.callback.PipelineIterator;
import org.parallelj.internal.util.sm.StateEvent;
import org.parallelj.internal.util.sm.StateListener;
import org.parallelj.internal.util.sm.StateMachines;
import org.parallelj.mirror.ProcessState;

/**
 * Represents a procedure bound to a {@link KProgram}.
 * 
 * @author Laurent Legrand
 * 
 */
public class SubProcessProcedure extends KProcedure {

	/**
	 * The sub program.
	 */
	KProgram subProgram;

	boolean isPipeline;

	String pipelineDataName;

	public class SubProcessCall extends KCall {

		public class SubProcessRunnable implements
				StateListener<KProcess, ProcessState>, Runnable {

			private SubProcessCall subProcessCall;

			protected SubProcessRunnable(SubProcessCall call) {
				this.subProcessCall = call;
			}

			public SubProcessCall getSubProcessCall() {
				return this.subProcessCall;
			}

			@Override
			public void run() {
				SubProcessCall.this.start();
				KProcess sub = SubProcessProcedure.this.subProgram
						.newProcess(SubProcessCall.this.getContext());

				if (isPipeline) {

					//getting count of list data and initializing the same 
					int dataCount = this.initializePipelineData();

					for (int count = 0; count < dataCount; count++) {
						subProgram.getInputCondition().produce(sub);
					}
				}

				sub.setParentId(SubProcessCall.this.getId());
				StateMachines.addStateListener(sub, this);
				if (KProcessor.currentProcessor() != null)
					KProcessor.currentProcessor().execute(sub);
			}

			/**
			 *  This will initialize data, and return count of the same
			 * @return
			 */
			private int initializePipelineData() {
				
				int dataCount = 0;

				Object context = SubProcessCall.this.getContext();

				// getting actual value of list
				Method readMethod = null;
				try {
					readMethod = new PropertyDescriptor(pipelineDataName,
							context.getClass()).getReadMethod();
				} catch (IntrospectionException e) {
					MessageKind.W0003.format(e);
				}
				Object invoke = null;
				try {
					invoke = readMethod.invoke(context);
				} catch (IllegalArgumentException e) {
					MessageKind.W0003.format(e);
				} catch (IllegalAccessException e) {
					MessageKind.W0003.format(e);
				} catch (InvocationTargetException e) {
					MessageKind.W0003.format(e);
				}

				//putting values into list
				if (invoke instanceof Iterable<?>) {
					
					if(Collection.class.isAssignableFrom(((Iterable) invoke).getClass())){
						dataCount = ((Collection<?>)invoke).size();
					}else{
						Iterator iterator = ((Iterable) invoke).iterator();
						while (iterator.hasNext()) {
							iterator.next();
							dataCount++;
						}
					}
				}

				// assign list into iterator 
				if (invoke != null) {
					PipelineIterator pipelineIterator = subProgram
							.getPipelineIteratorsMap().get(context);
					if (pipelineIterator == null) {
						pipelineIterator = new PipelineIterator();
					}
					pipelineIterator.setPipelineDataName(pipelineDataName);
					subProgram.getPipelineIteratorsMap().put(context,
							pipelineIterator);
					
					return dataCount - 1;
				} else {
					return dataCount;
				}
			}

			@Override
			public void stateChanged(StateEvent<KProcess, ProcessState> event) {
				switch (event.getState()) {
				case ABORTED:
				case TERMINATED:
				case COMPLETED:
					SubProcessCall.this.complete();
				}
			}
		}

		protected SubProcessCall(KProcess process) {
			super(SubProcessProcedure.this, process);
		}

		@Override
		public Runnable toRunnable() {
			return new SubProcessRunnable(this);
		}

	}

	@Override
	protected KCall newCall(KProcess process) {
		return new SubProcessCall(process);
	}

	/**
	 * Create a new {@link SubProcessProcedure}.
	 * 
	 * @param program
	 *            the program containing this procedure
	 */
	public SubProcessProcedure(KProgram program) {
		super(program);
	}

	/**
	 * @return the sub program
	 */
	public KProgram getSubProgram() {
		return subProgram;
	}

	/**
	 * Set the sub program
	 * 
	 * @param program
	 *            the sub program
	 */
	public void setSubProgram(KProgram program) {
		this.subProgram = program;
	}

	public boolean isPipeline() {
		return isPipeline;
	}

	public void setPipeline(boolean isPipeline) {
		this.isPipeline = isPipeline;
	}

	public String getPipelineDataName() {
		return pipelineDataName;
	}

	public void setPipelineDataName(String pipelineDataName) {
		this.pipelineDataName = pipelineDataName;
	}
}