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

package org.parallelj.internal.kernel.join;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.parallelj.internal.kernel.KCall;
import org.parallelj.internal.kernel.KCondition;
import org.parallelj.internal.kernel.KInputLink;
import org.parallelj.internal.kernel.KProcedure;
import org.parallelj.internal.kernel.KProcess;
import org.parallelj.internal.kernel.KProgram;
import org.parallelj.internal.kernel.callback.Entry;

public abstract class AbstractJoinTest {
	
	static Logger logger = Logger.getRootLogger();

	KProgram program;

	KProcedure procedure;

	List<KCondition> conditions = new ArrayList<KCondition>();

	@Before
	public void init() {
		this.program = new KProgram();
		this.procedure = new KProcedure(this.program);
		this.procedure.setEntry(new Entry() {
			
			@Override
			public void enter(KCall call) {
				logger.info("enter");
			}
		});
		this.conditions.clear();
	}
	
	protected KCondition[] prepare(int... values) {
		for (int i = 0; i < values.length; i++) {
			this.addCondition(values[i]);
		}
		return this.conditions.toArray(new KCondition[0]);
	}

	protected KCondition addCondition(int value) {
		KCondition condition = new KCondition(this.program, (short) value);
		this.conditions.add(condition);
		new KInputLink(condition, this.procedure);
		return condition;
	}

	protected void verify(KProcess instance, int... values) {
		for (int i = 0; i < conditions.size() && i < conditions.size(); i++) {
			Assert.assertTrue(String.format("value[%d] = %d; %d expected", i,
					conditions.get(i).size(instance), values[i]), conditions
					.get(i).size(instance) == values[i]);
		}
	}

}
