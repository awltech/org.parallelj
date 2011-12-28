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

import java.util.concurrent.Callable;

import org.parallelj.internal.MessageKind;
import org.parallelj.internal.kernel.KCall;
import org.parallelj.internal.kernel.KOutputParameter;
import org.parallelj.internal.kernel.KProcedure;
import org.parallelj.internal.kernel.KProcess;
import org.parallelj.internal.kernel.KProgram;

/**
 * Represents a procedure bound to a {@link Callable}.
 * 
 * @author Laurent Legrand
 * 
 */
public class CallableProcedure extends KProcedure {

	class CallableCall extends KCall {

		protected CallableCall(KProcess process) {
			super(CallableProcedure.this, process);
		}

		@Override
		public Runnable toRunnable() {

			return new Runnable() {

				@Override
				public void run() {
					CallableCall.this.start();
					try {
						CallableProcedure.this.result.set(CallableCall.this,
								((Callable<?>) CallableCall.this.getContext())
										.call());
					} catch (Exception e) {
						MessageKind.W0003.format(e);
						CallableCall.this.setException(e);
					} finally {
						CallableCall.this.complete();
					}
				}
			};
		}

	}

	/**
	 * {@link KOutputParameter} that will store the result of the
	 * {@link Callable#call()} method.
	 */
	KOutputParameter result = new KOutputParameter();

	/**
	 * Create a new {@link CallableProcedure}
	 * 
	 * @param program
	 *            the program that contains this procedure
	 */
	public CallableProcedure(KProgram program) {
		super(program);
		this.addOutputParameter(this.result);
	}

	@Override
	protected KCall newCall(KProcess process) {
		return new CallableCall(process);
	}

}