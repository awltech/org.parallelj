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
package org.parallelj.internal.kernel.split;

import org.junit.Test;
import org.parallelj.internal.kernel.KProcess;
import org.parallelj.internal.kernel.KProcessor;
import org.parallelj.internal.kernel.split.KAndSplit;

public class AndSplitTest extends AbstractSplitTest {

	@Test
	public void testTrueTrueSplit() {
		KAndSplit split = newAndSplit(true, true);
		this.procedure.setSplit(split);
		KProcess process = this.program.newProcess(null);
		new KProcessor().execute(process);
		this.verify(process, 1, 1);
	}

	@Test
	public void testTrueFalseSplit() {
		KAndSplit split = newAndSplit(true, false);
		this.procedure.setSplit(split);
		KProcess process = this.program.newProcess(null);
		new KProcessor().execute(process);
		this.verify(process, 1, 1);
	}

	@Test
	public void testFalseFalseSplit() {
		KAndSplit split = newAndSplit(false, false);
		this.procedure.setSplit(split);
		KProcess process = this.program.newProcess(null);
		new KProcessor().execute(process);
		this.verify(process, 1, 1);
	}

	KAndSplit newAndSplit(boolean... guards) {
		this.prepare(guards);
		return new KAndSplit(this.procedure);
	}

}
