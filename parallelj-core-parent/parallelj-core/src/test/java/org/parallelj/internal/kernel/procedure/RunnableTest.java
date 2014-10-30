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
import org.parallelj.internal.kernel.callback.Entry;
import org.parallelj.internal.kernel.callback.Exit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RunnableTest extends ProcedureTest<RunnableProcedure> {
	
	static Logger logger = LoggerFactory.getLogger("org.parallelj.internal");

	@Override
	public void setupProcedure() {
		procedure = new RunnableProcedure(program);
		procedure.setEntry(new Entry() {

			@Override
			public void enter(KCall execution) {
				logger.info("enter");
				execution.setContext(new Runnable() {

					@Override
					public void run() {
						logger.info("run");
					}
				});
			}
		});
		procedure.setExit(new Exit() {

			@Override
			public void exit(KCall execution) {
				logger.info("exit");
			}

		});
	}
}
