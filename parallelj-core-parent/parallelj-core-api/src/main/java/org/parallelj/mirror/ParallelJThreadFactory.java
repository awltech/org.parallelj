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

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Factory in charge of naming threads created so that they appear clearly when
 * performing a thread dump.
 * 
 */
public class ParallelJThreadFactory implements ThreadFactory {

	private static class Holder {
		private static final ParallelJThreadFactory INSTANCE = new ParallelJThreadFactory();
	}

	public static ParallelJThreadFactory getInstance() {
		return ParallelJThreadFactory.Holder.INSTANCE;
	}

	private static final String defaultThreadGroupName="//J-";
	private static final String defaultThreadName="//J-Thread-";
	
	/**
	 * The base name to use when creating threads using this factory.
	 */
	private String threadName=defaultThreadName;
	
	/**
	 * The counter for each created thread appended to the base name.
	 */
	private AtomicLong counter = new AtomicLong();
	
	/**
	 * The threadGroup for each new ParallelJ Thread.
	 */
	private ThreadGroup threadGroup;

	private ParallelJThreadFactory() {
		this.threadGroup=new ThreadGroup(defaultThreadGroupName);
		this.threadName=defaultThreadName;
	}

	@Override
	public Thread newThread(Runnable r) {
		return new Thread(this.threadGroup, r, this.threadName+counter.incrementAndGet());
	}

}