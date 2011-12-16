/*
 *     ParallelJ, framework for parallel computing
 *
 *     Copyright (C) 2010 Atos Worldline or third-party contributors as
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

import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

import java.util.concurrent.CountDownLatch;

import org.parallelj.launching.LaunchingMessageKind;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.matchers.EverythingMatcher;

/**
 * A launch entry of a Program.
 *
 */
public class Launch {
	/**
	 * The scheduler used to launch Programs.
	 */
	Scheduler scheduler;
	
	/**
	 * The result Object of this Launch.
	 */
	LaunchResult launchResult;
	
	/**
	 * The Program Adapter class.
	 */
	Class<? extends Job> jobClass;
	
	/**
	 * The Quartz JobBuilder for this Launch. 
	 */
	JobBuilder jobBuilder;
	
	/**
	 * The Quartz Job for this Launch. 
	 */
	JobDetail job;
	
	/**
	 * The TriggerBuilder for this Launch.
	 */
	TriggerBuilder<Trigger> triggerBuilder = newTrigger();
	
	/**
	 * The Trigger for this Launch.
	 */
	Trigger trigger;
	
	/**
	 * Time to wait for the JobId to be available when scheduling the Program.
	 */
	private static final long MSECONDS = 1L * 500L;

	/**
	 * Default Constructor.
	 * 
	 * @param scheduler The ParalleljScheduler
	 * @param jobClass The Program Adapter class
	 */
	public Launch(Scheduler scheduler, Class<? extends Job> jobClass) {
		this.scheduler = scheduler;
		this.jobClass = jobClass;
		this.jobBuilder = newJob(this.jobClass);
		
		this.job = jobBuilder.withIdentity(String.valueOf(jobBuilder),
				String.valueOf(jobBuilder)).build();
		
		this.trigger = triggerBuilder
				.withIdentity(String.valueOf(triggerBuilder),
						String.valueOf(triggerBuilder)).startNow().build();
	}
	
	/**
	 * Launch a Program and wait until it's terminated.
	 * 
	 * @return A Launch instance.
	 * @throws LaunchException When a SchedulerException occurred.
	 */
	public synchronized Launch synchLaunch() throws LaunchException {
		try {
			// Define a listener to get the jobId and to wait until the Job is completed
			AdapterJobListener listener = new AdapterJobListener(this.jobClass.getCanonicalName(), this.scheduler);
			this.scheduler.getListenerManager().addJobListener(listener, EverythingMatcher.allJobs());
			this.scheduler.getListenerManager().addSchedulerListener(listener);
		
			CountDownLatch latcher = createLatcher(listener);

			// Launch the Job
			this.scheduler.scheduleJob(this.job, this.trigger);
			this.scheduler.start();
			
			// Wait few seconds for the JobId to be available.
			try {
				Thread.sleep(MSECONDS);
			} catch (Exception e) {
			}
			
			LaunchingMessageKind.IQUARTZ0002.format(jobClass.getCanonicalName(), listener.getJobId());
			
			awaitingLatcher(latcher, listener);
			
			this.launchResult= new LaunchResult(listener.getJobId(), listener.getResult());
		} catch (SchedulerException e) {
			throw new LaunchException(e);
		}
		return this;
	}
	
	/**
	 * Launch a Program and continue.
	 * 
	 * @return A Launch instance.
	 * @throws LaunchException When a SchedulerException occurred.
	 */
	public synchronized Launch aSynchLaunch() throws LaunchException {
		try {
			// Define a listener to get the jobId
			AdapterJobListener listener = new AdapterJobListener(this.jobClass.getCanonicalName(), this.scheduler);
			this.scheduler.getListenerManager().addJobListener(listener, EverythingMatcher.allJobs());
			this.scheduler.getListenerManager().addSchedulerListener(listener);
		
			// Launch the Job
			this.scheduler.scheduleJob(this.job, this.trigger);
			this.scheduler.start();
			
			// Wait few seconds for the JobId to be available.
			try {
				Thread.sleep(MSECONDS);
			} catch (Exception e) {
			}
			
			LaunchingMessageKind.IQUARTZ0002.format(jobClass.getCanonicalName(), listener.getJobId());
			
			this.launchResult= new LaunchResult(listener.getJobId(), listener.getResult());
			//this.scheduler.getListenerManager().removeJobListener(listener.getName());
		} catch (SchedulerException e) {
			throw new LaunchException(e);
		}
		return this;
	}
	
	/**
	 * Create a CountDownLatch to be able to wait until a Program is terminated.
	 * 
	 * @param listener The Quartz listener used for this Launch.
	 * @return a CountDownLatch.
	 */
	private CountDownLatch createLatcher(AdapterJobListener listener) {
		CountDownLatch latcher = new CountDownLatch(1);
		listener.setLatcher(latcher);
		return latcher;
	}

	/**
	 * Wait for the CountDownLatch.
	 * 
	 * @param latcher The CountDownLatch
	 * @param listener The listener which decrements the count of the latch.
	 */
	private void awaitingLatcher(CountDownLatch latcher, AdapterJobListener listener) {
		if (latcher.getCount()>0) {
				try {
					latcher.await();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
		}
		listener.setLatcher(null);
	}
	
	/**
	 * Add a Quartz JobData to the one used to launch the Program.
	 * This JobData is used to initialize Programs arguments for launching.
	 * 
	 * @param jobDataMap A JobDatamap
	 * @return This Launch instance.
	 */
	public synchronized Launch addDatas(JobDataMap jobDataMap) {
		this.job.getJobDataMap().putAll(jobDataMap);
		return this;
	}

	/**
	 * Get the JobId generated by Quartz when launching the Program.
	 * 
	 * @return the JobId.
	 */
	public synchronized String getLaunchId() {
		return this.launchResult.getJobId();
	}

	/**
	 * Get the result Object of the Launch.
	 * 
	 * @return The result Object of the launch.
	 */
	public synchronized Object getLaunchResult() {
		return this.launchResult.getResult();
	}

}
