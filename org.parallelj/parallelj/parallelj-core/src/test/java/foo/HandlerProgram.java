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
import org.parallelj.Handler;
import org.parallelj.Program;

@Program
public class HandlerProgram {
	
	static Logger logger = Logger.getRootLogger();

	boolean enter, exit, handler, b = false;

	@AndJoin("begin")
	public Runnable a() {
		enter = true;
		return new Runnable() {

			@Override
			public void run() {
				throw new RuntimeException();
			}
		};
	}

	@AndSplit("end")
	public void a(Runnable r) {
		exit = false;
	}

	@Handler("a")
	@AndSplit("b")
	public void handler(Exception e) {
		handler = true;
		e.printStackTrace();
		logger.info("called: " + e);
	}

	@AndJoin
	@AndSplit("end")
	public void b() {
		b = true;
	}

}
