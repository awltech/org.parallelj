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
package org.parallelj.jmx;

import org.parallelj.mirror.ExecutorServiceKind;
import org.parallelj.mirror.Process;
import org.parallelj.mirror.Processor;
import org.parallelj.mirror.ProgramType;

/**
 * Provide //J management features through JMX.
 * 
 * All return values of methods are <a href="http://www.json.org/">JSON</a>
 * objects or arrays.
 * 
 * On the other hand, method parameters are simple literal.
 * 
 * @author Laurent Legrand
 */
public interface ManagementMBean {

	/**
	 * Return the list of {@link ProgramType}.
	 * 
	 * @return the list of programs as a JSON array.
	 */
	String getPrograms();

	/**
	 * Return a {@link ProgramType}.
	 * 
	 * @param programId
	 *            the program id
	 * @return a program as a JSON object
	 */
	String getProgram(String programId);

	/**
	 * Create a new {@link Processor}
	 * 
	 * @param kind
	 *            the kind of {@link Processor}. Value must correspond to a
	 *            {@link ExecutorServiceKind} value.
	 * @param nThreads
	 *            the number of threads in the thread pool. Used only if kind
	 *            parameter is {@link ExecutorServiceKind#FIXED_THREAD_POOL}.
	 * @return a new processor as a JSON object
	 */
	String newProcessor(String kind, int nThreads);

	/**
	 * Return a {@link Processor}
	 * 
	 * @param processorId
	 *            the id of the processor
	 * @return a processor as a JSON object
	 */
	String getProcessor(String processorId);

	/**
	 * Create a new {@link Process}
	 * 
	 * @param programId
	 *            the id of the {@link ProgramType}
	 * @param context
	 *            an XML representation of the context
	 * @return a process as a JSON object
	 */
	String newProcess(String programId, String context);

	/**
	 * Return a {@link Process}.
	 * 
	 * @param processId
	 *            the id of the process
	 * @return a process as a JSON object
	 */
	String getProcess(String processId);

	/**
	 * Execute a {@link Process} using a given {@link Processor}.
	 * 
	 * @param processorId
	 *            the processor id
	 * @param processId
	 *            the program id
	 * @return a JSON array containing the processor and the process
	 */
	String executeProcess(String processorId, String processId);

	/**
	 * Suspend a {@link Processor}
	 * 
	 * @param processorId
	 *            the id of the processor to suspend
	 * @return the processor as a JSON object
	 */
	String suspendProcessor(String processorId);

	/**
	 * Resume a {@link Processor}
	 * 
	 * @param processorId
	 *            the id of the processor to resume
	 * @return the processor as a JSON object
	 */
	String resumeProcessor(String processorId);

	/**
	 * Terminate a {@link Process}
	 * 
	 * @param processId
	 *            the id of the process to terminate
	 * @return the processor as a JSON object
	 */
	String terminateProcess(String processId);

	/**
	 * Abort a {@link Process}
	 * 
	 * @param processId
	 *            the id of the process to abort
	 * @return the processor as a JSON object
	 */
	String abortProcess(String processId);

}
