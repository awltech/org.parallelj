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
package org.parallelj.launching.programs;

import java.util.concurrent.Callable;

import org.parallelj.AndJoin;
import org.parallelj.AndSplit;
import org.parallelj.Begin;
import org.parallelj.Handler;
import org.parallelj.Program;
import org.parallelj.XorJoin;
import org.parallelj.launching.OnError;
import org.parallelj.launching.ProceduresOnError;

@Program
public class ProgramWithErrors {

	@Begin
	public Runnable processing1() {
		return new Runnable() {
			@Override
			public void run() {
				throw new RuntimeException();
			}
		};
	}
	
	@AndSplit(value = "processing2")
	public void processing1(Runnable runnable) {
		return;
	}
	
	@AndJoin
	public Callable<Void> processing2() {
		return new Callable<Void>() {

			@Override
			public Void call() throws Exception {
				throw new Exception();
			}
		};
	}
	
	@AndSplit(value = "processing3")
	public void processing2(Callable<Void> callable, Void value) {
		return;
	}
	
	@XorJoin
	public Callable<Void> processing3() {
		return new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				throw new Exception();
			}
		};
	}
	
	@AndSplit(value = "end")
	public void processing3(Callable<Void> callable, Void value) {
		return;
	}
	
	@Handler(value = "processing2")
	@AndSplit(value = "processing3")
	public void handler(Exception e) {
		return;
	}

	@OnError
	private ProceduresOnError onMynErrors;

	public ProceduresOnError getOnMynErrors() {
		return this.onMynErrors;
	}
}
