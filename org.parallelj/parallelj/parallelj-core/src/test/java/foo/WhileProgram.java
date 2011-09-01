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

package foo;

import org.apache.log4j.Logger;
import org.parallelj.AndJoin;
import org.parallelj.AndSplit;
import org.parallelj.Program;
import org.parallelj.While;

@Program
public class WhileProgram {
	
	static Logger logger = Logger.getRootLogger();

	int count;

	public WhileProgram(int max) {
		this.count = max;
	}

	@AndJoin("begin")
	@While("valid")
	public MyRunnable a() {
		MyRunnable runnable = new MyRunnable();
		logger.info(this + ": enter: " + runnable + ": " + (this.count));
		this.count--;
		return runnable;
	}

	@AndSplit("end")
	public void a(Runnable runnable) {
		logger.info(this + ": exit: " + runnable);
	}

	public boolean isValid() {
		return this.count > 0;
	}

}
