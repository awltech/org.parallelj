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

import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * Entry point for mirror interfaces.
 * 
 * 
 * @author Laurent Legrand
 * 
 */
public interface Reflection {

	/**
	 * Return the list of {@link ProgramType}
	 * 
	 * @return
	 */
	List<ProgramType> getPrograms();

	/**
	 * Return a new {@link Processor}
	 * 
	 * If executorKind is {@link ExecutorServiceKind#NONE},
	 * {@link ExecutorServiceKind#SINGLE_THREAD_EXECUTOR} or
	 * {@link ExecutorServiceKind#CACHED_THREAD_POOL} then args must be
	 * empty.
	 * 
	 * If executorKind is {@link ExecutorServiceKind#FIXED_THREAD_POOL} then
	 * args must be of type int and must be greater than 1.
	 * 
	 * If executorKind is {@link ExecutorServiceKind#PROVIDED} then args
	 * must be an {@link ExecutorService}.
	 * 
	 * @param executorKind
	 *                the kind of executor
	 * @param args
	 * @return
	 * @throws IllegalArgumentException if one of the arguments does not match
	 */
	Processor newProcessor(ExecutorServiceKind executorKind, Object... args) throws IllegalArgumentException;
	
	/**
	 * Add an {@link EventListener}
	 * 
	 * @param listener the listener to add
	 */
	void addEventListener(EventListener listener);
	
	/**
	 * Remove an {@link EventListener}
	 * 
	 * @param listener the listener to remove
	 */
	void removeEventListener(EventListener listener);

}
