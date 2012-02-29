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

import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;

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

	private static final String DEFAULT_GROUP_NAME = "DEFAULT";
	protected static final String DEFAULT_EXECUTOR_KEY = "EXECUTOR";

	/**
	 * The scheduler used to launch Programs.
	 */
	private Scheduler scheduler;

	/**
	 * The result Object of this Launch.
	 */
	private LaunchResult launchResult;

	/**
	 * The Program Adapter class.
	 */
	private Class<? extends Job> jobClass;

	/**
	 * The Quartz JobBuilder for this Launch.
	 */
	private JobBuilder jobBuilder;

	/**
	 * The Quartz Job for this Launch.
	 */
	private JobDetail job;

	private Job adapter;
	
	/**
	 * The Executor service to use when launching the program 
	 * associated to this Launch
	 */
	private Executor executorService = null;

	/**
	 * The TriggerBuilder for this Launch.
	 */
	private TriggerBuilder<Trigger> triggerBuilder = newTrigger();

	/**
	 * The Trigger for this Launch.
	 */
	private Trigger trigger;

	/**
	 * Time to wait for the JobId to be available when scheduling the Program.
	 */
	private static final long MSECONDS = 1L * 500L;

	/**
	 * Default Constructor.
	 * 
	 * @param scheduler
	 *            The ParalleljScheduler
	 * @param jobClass
	 *            The Program Adapter class
	 * @throws LaunchException
	 */
	public Launch(final Scheduler scheduler, final Class<?> jobClass) throws LaunchException {
		this(scheduler, jobClass, null);
	}

	@SuppressWarnings("unchecked")
	public Launch(final Scheduler scheduler, final Class<?> jobClass, ExecutorService executorService)
			throws LaunchException {
		this.scheduler = scheduler;
		this.executorService = executorService;
		try {
			this.jobClass = (Class<? extends Job>) jobClass;
		} catch (ClassCastException e) {
			LaunchingMessageKind.ELAUNCH0001.format(jobClass, e);
			throw new LaunchException(e);
		}
		this.jobBuilder = newJob(this.jobClass);

		this.job = jobBuilder.withIdentity(this.jobClass.getCanonicalName(),
				DEFAULT_GROUP_NAME).build();

		// If an ExecutorService was specified, 
		// we put it in the JobDataMap to be able to use it when the program will be launched.
		if (this.executorService!=null) {
			this.job.getJobDataMap().put(DEFAULT_EXECUTOR_KEY, executorService);
		}
		
		this.trigger = triggerBuilder
				.withIdentity(String.valueOf(triggerBuilder),
						String.valueOf(triggerBuilder)).startNow().build();
	}

	/**
	 * Launch a Program and wait until it's terminated.
	 * 
	 * @return A Launch instance.
	 * @throws LaunchException
	 *             When a SchedulerException occurred.
	 */
	public Launch synchLaunch() throws LaunchException {
		try {
			// Define a listener to get the jobId and to wait until the Job is
			// completed
			final AdapterJobListener listener = new AdapterJobListener(
					this.jobClass.getCanonicalName(), this.scheduler);
			this.scheduler.getListenerManager().addJobListener(listener,
					EverythingMatcher.allJobs());
			this.scheduler.getListenerManager().addSchedulerListener(listener);

			final CountDownLatch latcher = createLatcher(listener);

			// Launch the Job
			this.scheduler.scheduleJob(this.job, this.trigger);
			this.scheduler.start();

			// Wait few seconds for the JobId to be available.
			try {
				Thread.sleep(MSECONDS);
			} catch (Exception e) {
				LaunchingMessageKind.EREMOTE0009.format(e);
			}

			LaunchingMessageKind.IQUARTZ0002.format(
					jobClass.getCanonicalName(), listener.getJobId());

			awaitingLatcher(latcher, listener);

			this.adapter = listener.getAdapter();
			this.launchResult = new LaunchResult(listener.getJobId(),
					listener.getResult());
			// Object obj = this.launchResult.getResult();
			// System.out.println(obj);
		} catch (SchedulerException e) {
			throw new LaunchException(e);
		}
		return this;
	}

	/**
	 * Launch a Program and continue.
	 * 
	 * @return A Launch instance.
	 * @throws LaunchException
	 *             When a SchedulerException occurred.
	 */
	public Launch aSynchLaunch() throws LaunchException {
		try {
			// Define a listener to get the jobId
			final AdapterJobListener listener = new AdapterJobListener(
					this.jobClass.getCanonicalName(), this.scheduler);
			this.scheduler.getListenerManager().addJobListener(listener,
					EverythingMatcher.allJobs());
			this.scheduler.getListenerManager().addSchedulerListener(listener);

			// Launch the Job
			this.scheduler.scheduleJob(this.job, this.trigger);
			this.scheduler.start();

			// Wait few seconds for the JobId to be available.
			try {
				Thread.sleep(MSECONDS);
			} catch (Exception e) {
				LaunchingMessageKind.EREMOTE0009.format(e);
			}
			this.adapter = listener.getAdapter();

			LaunchingMessageKind.IQUARTZ0002.format(
					jobClass.getCanonicalName(), listener.getJobId());

			this.launchResult = new LaunchResult(listener.getJobId(),
					listener.getResult());
			// this.scheduler.getListenerManager().removeJobListener(listener.getName());
		} catch (SchedulerException e) {
			throw new LaunchException(e);
		}
		return this;
	}

	/**
	 * Create a CountDownLatch to be able to wait until a Program is terminated.
	 * 
	 * @param listener
	 *            The Quartz listener used for this Launch.
	 * @return a CountDownLatch.
	 */
	private CountDownLatch createLatcher(final AdapterJobListener listener) {
		final CountDownLatch latcher = new CountDownLatch(1);
		listener.setLatcher(latcher);
		return latcher;
	}

	/**
	 * Wait for the CountDownLatch.
	 * 
	 * @param latcher
	 *            The CountDownLatch
	 * @param listener
	 *            The listener which decrements the count of the latch.
	 */
	private void awaitingLatcher(final CountDownLatch latcher,
			final AdapterJobListener listener) {
		if (latcher.getCount() > 0) {
			try {
				latcher.await();
			} catch (InterruptedException e) {
				LaunchingMessageKind.EREMOTE0009.format(e);
			}
		}
		listener.setLatcher(null);
	}

	/**
	 * Add a Quartz JobData to the one used to launch the Program. This JobData
	 * is used to initialize Programs arguments for launching.
	 * 
	 * @param jobDataMap
	 *            A JobDatamap
	 * @return This Launch instance.
	 */
	public synchronized Launch addDatas(final JobDataMap jobDataMap) {
		this.job.getJobDataMap().putAll(jobDataMap);
		return this;
	}

	/**
	 * Get the JobId generated by Quartz when launching the Program.
	 * 
	 * @return the JobId.
	 */
	public String getLaunchId() {
		return this.launchResult.getJobId();
	}

	/**
	 * Get the result Object of the Launch.
	 * 
	 * @return The result Object of the launch.
	 */
	public JobDataMap getLaunchResult() {
		return this.launchResult.getResult();
	}

	/**
	 * @return
	 */
	public Job getAdapter() {
		return adapter;
	}

}
