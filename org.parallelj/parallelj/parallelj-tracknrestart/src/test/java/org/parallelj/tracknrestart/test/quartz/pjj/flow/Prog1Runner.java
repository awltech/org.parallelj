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
package org.parallelj.tracknrestart.test.quartz.pjj.flow;

import java.util.List;
import java.util.concurrent.Executors;

import org.parallelj.Programs;

public class Prog1Runner {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Prog1 prog1 = new Prog1();
		List<String> data1 = prog1.getData1();
		data1.add("a");
		data1.add("b");
		data1.add("c");
		Programs.as(prog1).execute(Executors.newCachedThreadPool()).join();
	}

}
