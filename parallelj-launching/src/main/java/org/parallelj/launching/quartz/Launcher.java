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

import org.parallelj.launching.LaunchingMessageKind;
import org.quartz.SchedulerException;

/**
 * Entry point for launching Programs.
 */
public final class Launcher {
	/**
	 * The ParalleljScheduler used for Program launching.
	 */
	private ParalleljScheduler scheduler;
	
	/**
	 * The unique instance of Launcher.
	 */
	private static Launcher instance;
	
	/**
	 * Time to wait after the ParallelJ scheduler started to be able to launch Programs.
	 */
	private static final long FIVE_SECONDS = 5L * 1000L;

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
	 * @throws LaunchException If a SchedulerException occurred when initializing the scheduler.
	 */
	private Launcher() throws LaunchException {
		try {
			this.scheduler = new ParalleljSchedulerFactory().getScheduler();
			this.scheduler.start();
			
			// wait 5 seconds to give our jobs a chance to run
			try {
				Thread.sleep(FIVE_SECONDS);
			} catch (Exception e) {
				LaunchingMessageKind.EREMOTE0009.format(e);
			}
		} catch (SchedulerException e) {
			throw new LaunchException(e);
		}
	}
	
	/**
	 * Create a new instance of Launch.
	 * 
	 * @param jobClass The Program Adapter class.
	 * @return An instance of Launch.
	 * @throws LaunchException 
	 */
	public synchronized Launch newLaunch(final Class<?> jobClass) throws LaunchException {
		return new Launch(this.scheduler, jobClass);
	}
	
	/**
	 * Create a new instance of Launch.
	 * 
	 * @param jobClass The Program Adapter class.
	 * @param executorService The ExecutorService instance to use.
	 * @return An instance of Launch.
	 * @throws LaunchException 
	 */
	public synchronized Launch newLaunch(final Class<?> jobClass, ExecutorService executorService) throws LaunchException {
		return new Launch(this.scheduler, jobClass, executorService);
	}
	
	/**
	 * Terminate the unique instance of Launcher by stopping the Quartz scheduler.
	 * This method have to be called for a simple Program launching.
	 */
	public void complete() {
		try {
			this.scheduler.shutdown();
		} catch (SchedulerException e) {
			LaunchingMessageKind.EQUARTZ0005.format(e);
		}
	}
	
}
