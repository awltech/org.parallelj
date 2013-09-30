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
package org.parallelj.launching;

import java.util.concurrent.ExecutorService;

import org.parallelj.launching.internal.LaunchImpl;

/**
 * Entry point for launching Programs.
 */
public final class Launcher {

	/**
	 * Static internal class, in charge of holding the Singleton instance.
	 */
	private static class SingletonHolder {
		static Launcher instance = new Launcher();
	}

	/**
	 * Private Constructor for the unique Launcher instance.
	 */
	private Launcher() {
	}
	
	/**
	 * Get the unique instance of Launcher.
	 * 
	 * @return The Launcher unique instance.
	 * @throws LaunchException If a SchedulerException occurred when creating instance.
	 */
	public static Launcher getLauncher() throws LaunchException {
		return SingletonHolder.instance;
	}

	@Deprecated
	public void complete() {
		// Do Nothing..
	}

	public  <T> Launch<T> newLaunch(Class<? extends T> class1) throws LaunchException {
		return newLaunch(class1, null);
	}
	
	/**
	 * Create a new instance of Launch.
	 * 
	 * @param jobClass The Program Adapter class.
	 * @param executorService The ExecutorService instance to use.
	 * @return An instance of Launch.
	 * @throws LaunchException 
	 */
	public synchronized <T> Launch<T> newLaunch(final Class<? extends T> jobClass, ExecutorService executorService) throws LaunchException {
		return new LaunchImpl<T>(jobClass, executorService);
	}
	
	
}
