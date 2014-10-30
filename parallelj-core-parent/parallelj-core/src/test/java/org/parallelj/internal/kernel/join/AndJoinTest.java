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
package org.parallelj.internal.kernel.join;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.parallelj.internal.kernel.KProcess;
import org.parallelj.internal.kernel.KProcessor;

public class AndJoinTest extends AbstractJoinTest {

	@Test
	public void testOneOneJoin() {
		KAndJoin join = newAndJoin(1, 1);
		this.procedure.setJoin(join);
		KProcess process = this.program.newProcess(null);
		assertTrue(join.isEnabled(process));
		new KProcessor().execute(process);
		this.verify(process, 0, 0);
		assertFalse(join.isEnabled(process));
	}

	@Test
	public void testZeroOneJoin() {
		KAndJoin join = newAndJoin(0, 1);
		this.procedure.setJoin(join);
		KProcess process = this.program.newProcess(null);
		new KProcessor().execute(process);
		assertFalse(join.isEnabled(process));
	}

	@Test
	public void testZeroZeroJoin() {
		KAndJoin join = newAndJoin(0, 0);
		this.procedure.setJoin(join);
		KProcess process = this.program.newProcess(null);
		new KProcessor().execute(process);
		assertFalse(join.isEnabled(process));
	}

	KAndJoin newAndJoin(int... values) {
		this.prepare(values);
		return new KAndJoin(this.procedure);
	}

}
