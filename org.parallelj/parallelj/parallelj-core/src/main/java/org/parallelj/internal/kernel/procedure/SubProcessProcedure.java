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

import org.parallelj.internal.kernel.KCall;
import org.parallelj.internal.kernel.KProcedure;
import org.parallelj.internal.kernel.KProcess;
import org.parallelj.internal.kernel.KProcessor;
import org.parallelj.internal.kernel.KProgram;
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

	class SubProcessCall extends KCall {

		class SubProcessRunnable implements
				StateListener<KProcess, ProcessState>, Runnable {

			protected SubProcessRunnable() {
			}

			@Override
			public void run() {
				SubProcessCall.this.start();
				KProcess sub = SubProcessProcedure.this.subProgram
						.newProcess(SubProcessCall.this.getContext());
				sub.setParentId(SubProcessCall.this.getId());
				StateMachines.addStateListener(sub, this);
				if (KProcessor.currentProcessor() != null)
					KProcessor.currentProcessor().execute(sub);
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
			return new SubProcessRunnable();
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

}