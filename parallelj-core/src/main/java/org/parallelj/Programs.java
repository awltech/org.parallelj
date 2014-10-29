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
package org.parallelj;

import java.util.concurrent.ExecutorService;

import org.parallelj.mirror.Process;
import org.parallelj.mirror.ProcessState;

/**
 * Helper class for classes annotated with {@link Program}.
 * 
 * @author Atos Worldline
 * 
 */
public class Programs {
	
	/**
	 * Hepler interface to {@link Process} and {@link org.parallelj.mirror.Processor}
	 *  
	 * @author Laurent Legrand
	 *
	 * @param <E>
	 */
	public interface ProcessHelper<E> {
		
		/**
		 * Return the context bound to the process.
		 * 
		 * @return the context bound to the process.
		 */
		public E context();

		/**
		 * Execute the process with the current thread.
		 * 
		 * @return itself for method chaining.
		 */
		public ProcessHelper<E> execute();

		/**
		 * Execute the process with a specific executor service.
		 * 
		 * @param service
		 *            the {@link ExecutorService} that will be used to execute
		 *            procedure call.
		 * @return itself for method chaining.
		 */
		public ProcessHelper<E> execute(ExecutorService service);

		/**
		 * Abort the process.
		 * 
		 * @return itself for method chaining.
		 */
		public ProcessHelper<E> abort();

		/**
		 * Terminate the process.
		 * 
		 * @return itself for method chaining.
		 */
		public ProcessHelper<E> terminate();

		/**
		 * Suspend the execution of the process.
		 * 
		 * @return itself for method chaining.
		 */
		public ProcessHelper<E> suspend();

		/**
		 * Resume the execution of the process.
		 * 
		 * @return itself for method chaining.
		 */
		public ProcessHelper<E> resume();

		/**
		 * Wait for the completion of the process.
		 * 
		 * @return itself for method chaining.
		 */
		public ProcessHelper<E> join();
		
		/**
		 * Return the underlying {@link Process}
		 * 
		 * @return the underlying {@link Process}
		 */
		public Process getProcess();
		
		/**
		 * Return the state of the {@link Process}
		 * 
		 * @return the state of the {@link Process}
		 */
		public ProcessState getState();
		
	}

	/**
	 * Adapt an object to a {@link Process}.
	 * 
	 * @param <E>
	 * @param e
	 *            the object to adapt
	 * @return the corresponding process
	 * @throws IllegalArgumentException
	 *             if the class of the parameter is not annotated by
	 *             {@link Program}
	 */
	public static <E> ProcessHelper<E> as(E e)
			throws IllegalArgumentException {
		return null;
	}

}
