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

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.parallelj.internal.MessageKind;

/**
 * The kind of {@link ExecutorService} that can be used by the {@link Processor}
 * 
 * @author Laurent Legrand
 * 
 */
public enum ExecutorServiceKind {

	/**
	 * Use the current thread.
	 */
	NONE,

	/**
	 * Use a {@link Executors#newSingleThreadExecutor()}.
	 */
	SINGLE_THREAD_EXECUTOR,

	/**
	 * Use a {@link Executors#newCachedThreadPool()}.
	 */
	CACHED_THREAD_POOL,

	/**
	 * Use a {@link Executors#newFixedThreadPool(int)}.
	 */
	FIXED_THREAD_POOL,

	/**
	 * Use a provided executor service
	 */
	PROVIDED;

	public ExecutorService create(int size, String className) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		switch (this) {
			case PROVIDED:
				if (className==null) {
					throw new IllegalArgumentException(MessageKind.E0001.format(
							this, className));
				}
				return (ExecutorService) Class.forName(String.valueOf(className)).newInstance();
			case NONE:
				return null;
			case SINGLE_THREAD_EXECUTOR:
				return Executors.newSingleThreadExecutor(ParallelJThreadFactory.getInstance());
			case CACHED_THREAD_POOL:
				return Executors.newCachedThreadPool(ParallelJThreadFactory.getInstance());
			case FIXED_THREAD_POOL:
				if (size == 0) {
					throw new IllegalArgumentException(MessageKind.E0001.format(
							this, size));
				}
				return Executors.newFixedThreadPool(size);
		}
		return null;
	}

}
