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

package org.parallelj.internal.kernel.misc;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.parallelj.internal.kernel.KCall;
import org.parallelj.internal.kernel.KCondition;
import org.parallelj.internal.kernel.KInputLink;
import org.parallelj.internal.kernel.KOutputLink;
import org.parallelj.internal.kernel.KProcedure;
import org.parallelj.internal.kernel.KProcess;
import org.parallelj.internal.kernel.KProcessor;
import org.parallelj.internal.kernel.KProgram;
import org.parallelj.internal.kernel.callback.Entry;
import org.parallelj.internal.kernel.join.AbstractJoinTest;
import org.parallelj.internal.kernel.join.KAndJoin;
import org.parallelj.internal.kernel.split.KAndSplit;

public class PipelineTest extends AbstractJoinTest {
	
	static Logger logger = Logger.getRootLogger();

	@Test
	public void testPipelineWithOneNode() {

		KProgram program = new KProgram();
		KProcedure a = new KProcedure(program);

		a.setName("a");
		a.setCapacity((short) 10);
		a.setEntry(new Entry() {

			@Override
			public void enter(KCall execution) {
				logger.info("a");
			}
		});

		KProcedure b = new KProcedure(program);
		b.setName("b");
		b.setEntry(new Entry() {

			@Override
			public void enter(KCall execution) {
				logger.info("b");
			}
		});

		KCondition a2b = new KCondition(program);

		new KInputLink(program.getInputCondition(), a);
		new KOutputLink(a, a2b);
		new KInputLink(a2b, b);
		new KOutputLink(b, program.getOutputCondition());

		a.setJoin(new KAndJoin(a));
		a.setSplit(new KAndSplit(a));

		b.setJoin(new KAndJoin(b));
		b.setSplit(new KAndSplit(b));

		new KPipeline(program, a, b);
		KProcess process = program.newProcess(null);
		for (int i = 0; i < 3; i++) {
			program.getInputCondition().produce(process);
		}
		
		new KProcessor().execute(process);

	}

	@Test
	public void testPipelineWithTwoNodes() {
		// KPipeline one = this.prepare(null, 2);
		// KPipeline two = this.prepare(one, 2);
		// assertTrue(one.isEnabled(this.process.newProcess(null)));
		// one.join(this.process.newProcess(null));
		// assertFalse(one.isEnabled(this.process.newProcess(null)));
		// assertTrue(two.isEnabled(this.process.newProcess(null)));
		// two.join(this.process.newProcess(null));
		// assertTrue(one.isEnabled(this.process.newProcess(null)));
		// one.join(this.process.newProcess(null));
		// assertFalse(one.isEnabled(this.process.newProcess(null)));
		// two.join(this.process.newProcess(null));
		// assertFalse(one.isEnabled(this.process.newProcess(null)));
	}

	// KPipeline prepare(KPipeline previous, int value) {
	//
	// KAbstractJoin join = (previous == null) ? new KAndJoin(this.process,
	// this.prepare(value)) : new KAndJoin(
	// this.process, this.addStates(value));
	// KPipeline pipeline = new KPipeline(this.process, join, previous, (short)
	// 1);
	// return pipeline;
	// }

}
