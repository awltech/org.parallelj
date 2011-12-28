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
package org.parallelj.internal.kernel;

import org.junit.Test;
import org.parallelj.internal.kernel.join.KAndJoin;
import org.parallelj.internal.kernel.split.KAndSplit;


public class ExHandlingTest {
	
	class MyProcedure extends KProcedure {

		public MyProcedure(KProgram program) {
			super(program);
		}
		
		@Override
		protected KCall newCall(KProcess process) {
			return new KCall(this, process) {
				@Override
				public Runnable toRunnable() {
					return new Runnable() {
						@Override
						public void run() {
							start();
							setException(new Exception());
							complete();
						}
					};
				}
			};
		}
		
	}
	
	@Test
	public void test() {
		KProgram program = new KProgram();
		KProcedure procedure = new MyProcedure(program);
		new KInputLink(program.getInputCondition(), procedure);
		new KOutputLink(procedure, program.getOutputCondition());
		procedure.setJoin(new KAndJoin(procedure));
		procedure.setSplit(new KAndSplit(procedure));
		KHandler handler = new KHandler(program) {
			protected KCall newCall(KProcess process) {
				return super.newCall(process);
			};
		};
		procedure.setHandler(handler);
		
		new KProcessor().execute(program.newProcess(null));
	}

}
