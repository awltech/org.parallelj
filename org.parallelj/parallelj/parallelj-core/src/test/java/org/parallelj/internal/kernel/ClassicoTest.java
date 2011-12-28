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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.parallelj.internal.kernel.callback.Entry;
import org.parallelj.internal.kernel.join.KAndJoin;
import org.parallelj.internal.kernel.join.KXorJoin;
import org.parallelj.internal.kernel.split.KAndSplit;
import org.parallelj.internal.kernel.split.KXorSplit;

public class ClassicoTest {

	int aCount, bCount, cCount, dCount;

	KProgram program;

	KProcedure a, b, c, d;

	@Before
	public void init() {
		aCount = bCount = cCount = dCount = 0;
		
		program = new KProgram();
		
		a = new KProcedure(program);
		a.setEntry(new Entry() {

			@Override
			public void enter(KCall execution) {
				aCount++;
			}
		});

		b = new KProcedure(program);
		b.setEntry(new Entry() {

			@Override
			public void enter(KCall execution) {
				bCount++;
			}
		});
		c = new KProcedure(program);
		c.setEntry(new Entry() {

			@Override
			public void enter(KCall execution) {
				cCount++;
			}
		});
		d = new KProcedure(program);
		d.setEntry(new Entry() {

			@Override
			public void enter(KCall execution) {
				dCount++;
			}
		});
		
		KCondition a2b = new KCondition(program);
		KCondition a2c = new KCondition(program);
		KCondition b2d = new KCondition(program);
		KCondition c2d = new KCondition(program);
		
		new KInputLink(program.getInputCondition(), a);
		new KOutputLink(a, a2b);
		new KOutputLink(a, a2c);
		
		new KInputLink(a2b, b);
		new KOutputLink(b, b2d);
		
		new KInputLink(a2c, c);
		new KOutputLink(c, c2d);
		
		new KInputLink(c2d, d);
		new KInputLink(b2d, d);
		new KOutputLink(d, program.getOutputCondition());
	}

	@Test
	public void andand() {
		a.setJoin(new KAndJoin(a));
		a.setSplit(new KAndSplit(a));

		b.setJoin(new KAndJoin(b));
		b.setSplit(new KAndSplit(b));

		c.setJoin(new KAndJoin(c));
		c.setSplit(new KAndSplit(c));

		d.setJoin(new KAndJoin(d));
		d.setSplit(new KAndSplit(d));

		KProcess process = program.newProcess(null);
		new KProcessor().execute(process);
		Assert.assertEquals(aCount, 1);
		Assert.assertEquals(bCount, 1);
		Assert.assertEquals(cCount, 1);
		Assert.assertEquals(dCount, 1);
	}

	@Test
	public void andxor() {
		a.setJoin(new KAndJoin(a));
		a.setSplit(new KAndSplit(a));

		b.setJoin(new KAndJoin(b));
		b.setSplit(new KAndSplit(b));

		c.setJoin(new KAndJoin(c));
		c.setSplit(new KAndSplit(c));

		d.setJoin(new KXorJoin(d));
		d.setSplit(new KAndSplit(d));

		KProcess process = program.newProcess(null);
		new KProcessor().execute(process);
		Assert.assertEquals(aCount, 1);
		Assert.assertEquals(bCount, 1);
		Assert.assertEquals(cCount, 1);
		Assert.assertEquals(dCount, 2);
	}

	@Test
	public void xorxor() {
		a.setJoin(new KAndJoin(a));
		a.setSplit(new KXorSplit(a));

		b.setJoin(new KAndJoin(b));
		b.setSplit(new KAndSplit(b));

		c.setJoin(new KAndJoin(c));
		c.setSplit(new KAndSplit(c));

		d.setJoin(new KXorJoin(d));
		d.setSplit(new KAndSplit(d));

		KProcess process = program.newProcess(null);
		new KProcessor().execute(process);
		Assert.assertEquals(aCount, 1);
		Assert.assertEquals(bCount, 1);
		Assert.assertEquals(cCount, 0);
		Assert.assertEquals(dCount, 1);
	}

}
