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
package org.parallelj.mirror;

/**
 * Represents an occurrence of a {@link ProgramType}
 * 
 * @author Laurent Legrand
 * 
 */
public interface Process extends Element, Machine<ProcessState> {

	/**
	 * @return the program type.
	 */
	ProgramType getProgram();

	/**
	 * Return the context bound to the process.
	 * 
	 * @return the context bound to the process.
	 */
	public Object getContext();

	/**
	 * Return the {@link Processor} that executes this process.
	 * 
	 * @return the {@link Processor} that executes this process.
	 *         <code>null</code> if this process has not been started or if it
	 *         is in final state
	 */
	public Processor getProcessor();

	/**
	 * Abort the process.
	 */
	public void abort();

	/**
	 * Terminate the process.
	 */
	public void terminate();

}
