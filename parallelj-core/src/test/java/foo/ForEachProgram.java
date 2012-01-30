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
package foo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import org.apache.log4j.Logger;
import org.parallelj.AndJoin;
import org.parallelj.AndSplit;
import org.parallelj.Capacity;
import org.parallelj.ForEach;
import org.parallelj.Program;

@Program
public class ForEachProgram {
	
	static Logger logger = Logger.getRootLogger();

	Collection<String> collection = new ArrayList<String>();

	public ForEachProgram() {
		//collection = Arrays.asList("a", "b", "c");
		for (int i = 0; i < 100; i++) {
			this.collection.add("" + i);
		}
	}

	@Capacity(20)
	@AndJoin("begin")
	public MyRunnable a(@ForEach("collection") String s) {
		MyRunnable runnable = new MyRunnable();
		logger.info(this + ": enter: " + runnable + ": " + s);
		return runnable;
	}

	@AndSplit("end")
	public void a(Runnable runnable) {
		logger.info(this + ": exit: " + runnable);
	}

}
