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
package org.parallelj.launching.quartz;

import java.util.concurrent.ExecutorService;

import org.parallelj.launching.quartz.Launch;
import org.parallelj.launching.quartz.LaunchException;
import org.parallelj.launching.quartz.Launcher;

/**
 * Entry point for launching Programs.
 */
@Deprecated
public final class Launcher {

	/**
	 * The unique instance of Launcher.
	 */
	private static Launcher instance;
	
	/**
	 * Get the unique instance of Launcher.
	 * 
	 * @return The Launcher unique instance.
	 * @throws LaunchException If a SchedulerException occurred when creating instance.
	 */
	public static synchronized Launcher getLauncher() throws LaunchException {
		if (instance == null) {
			instance = new Launcher();
		}
		return instance;
	}
	
	/**
	 * Private Constructor for the unique Launcher instance.
	 * 
	 * @throws LaunchException If an Exception occurred.
	 */
	private Launcher() throws LaunchException {
	}
	
	/**
	 * Create a new instance of Launch.
	 * 
	 * @param jobClass The Program Adapter class.
	 * @return An instance of Launch.
	 * @throws LaunchException 
	 */
	public synchronized Launch newLaunch(final Class<?> jobClass) throws LaunchException {
		return new Launch(jobClass);
	}
	
	/**
	 * Create a new instance of Launch.
	 * 
	 * @param jobClass The Program Adapter class.
	 * @param executorService The ExecutorService instance to use.
	 * 
	 * @return An instance of Launch.
	 * @throws LaunchException 
	 */
	public synchronized Launch newLaunch(final Class<?> jobClass, ExecutorService executorService) throws LaunchException {
		return new Launch(jobClass, executorService);
	}
	
	/**
	 * Terminate the unique instance of Launcher by stopping the Quartz scheduler.
	 * This method have to be called for a simple Program launching.
	 */
	public void complete() {
		// Do nothing...
	}
	
}
