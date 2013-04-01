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

import org.parallelj.internal.MessageKind;
import org.parallelj.internal.kernel.KCall;
import org.parallelj.internal.kernel.KProcedure;
import org.parallelj.internal.kernel.KProcess;
import org.parallelj.internal.kernel.KProgram;
import org.parallelj.mirror.HandlerLoopPolicy;

/**
 * Represents a procedure bound to a {@link Runnable}.
 * 
 * @author Atos Worldline
 */
public class RunnableProcedure extends KProcedure {

	/**
	 * The RunnableCall expects to have the {@link #getContext() context} an
	 * implementation of {@link Runnable}.
	 * 
	 * In its {@link #run()} method, it will delegate to the
	 * {@link #getContext() context}.
	 * 
	 * @author Atos Worldline
	 * 
	 */
	class RunnableCall extends KCall {

		class RunnableCallRunnable implements Runnable {
			private RunnableCall runnableCall;
			
			public RunnableCallRunnable(RunnableCall runnableCall) {
				this.runnableCall = runnableCall;
			}
			
			public RunnableCall getRunnableCall() {
				return runnableCall;
			}

			@Override
			public void run() {
				RunnableCall.this.start();
				try {
					((Runnable) RunnableCall.this.getContext()).run();
				} catch (Exception e) {
					MessageKind.W0003.format(e);
					RunnableCall.this.setException(e);
					if (RunnableProcedure.this.getHandler()!=null 
							&& RunnableProcedure.this.getHandler().getHandlerLoopPolicy()==HandlerLoopPolicy.TERMINATE) {
						RunnableProcedure.this.setIsError(true);
					}
				} finally {
					RunnableCall.this.complete();
				}
			}
			
		}
		
		protected RunnableCall(KProcess process) {
			super(RunnableProcedure.this, process);
		}

		@Override
		public Runnable toRunnable() {
			return new RunnableCallRunnable(this);
		}
	}

	/**
	 * Create a new {@link RunnableProcedure}
	 * 
	 * @param program
	 *            the program containing this procedure
	 */
	public RunnableProcedure(KProgram program) {
		super(program);
	}

	@Override
	protected KCall newCall(KProcess process) {
		return new RunnableCall(process);
	}

}