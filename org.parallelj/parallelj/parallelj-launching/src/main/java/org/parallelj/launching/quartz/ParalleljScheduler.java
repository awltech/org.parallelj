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

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.parallelj.internal.conf.ParalleljConfiguration;
import org.parallelj.internal.conf.ParalleljConfigurationManager;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobKey;
import org.quartz.ListenerManager;
import org.quartz.Scheduler;
import org.quartz.SchedulerContext;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.SchedulerMetaData;
import org.quartz.Trigger;
import org.quartz.Trigger.TriggerState;
import org.quartz.TriggerKey;
import org.quartz.UnableToInterruptJobException;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.matchers.GroupMatcher;
import org.quartz.spi.JobFactory;

/**
 * This class is a Parallelj implementation of a Quartz Scheduler. It allows to
 * enrich default Quartz Scheduler
 * 
 */
public class ParalleljScheduler implements Scheduler {

	/**
	 * A Quartz Scheduler
	 */
	private Scheduler scheduler;

	/**
	 * The ParallelJ configuration Object
	 */
	private ParalleljConfiguration configuration;

	/**
	 * Default constructor for the ParalleljScheduler
	 * 
	 * @throws SchedulerException
	 */
	public ParalleljScheduler() throws SchedulerException {
		super();
		SchedulerFactory schedFact = new StdSchedulerFactory();
		this.scheduler = schedFact.getScheduler();
		initialize();
	}

	/**
	 * Method initialization for the ParalleljScheduler
	 */
	private void initialize() {
		// Load Parallelj Configuration file
		this.configuration = ParalleljConfigurationManager.getConfiguration();
	}

	private void stop() {
	}

	/**
	 * 
	 * Getter method for the Configuration Object
	 * 
	 * @return the configuration
	 */
	public final ParalleljConfiguration getConfiguration() {
		return this.configuration;
	}

	/* (non-Javadoc)
	 * @see org.quartz.Scheduler#getSchedulerName()
	 */
	@Override
	public final String getSchedulerName() throws SchedulerException {
		return this.scheduler.getSchedulerName();
	}

	/* (non-Javadoc)
	 * @see org.quartz.Scheduler#getSchedulerInstanceId()
	 */
	@Override
	public final String getSchedulerInstanceId() throws SchedulerException {
		return this.scheduler.getSchedulerInstanceId();
	}

	/* (non-Javadoc)
	 * @see org.quartz.Scheduler#getContext()
	 */
	@Override
	public final SchedulerContext getContext() throws SchedulerException {
		return this.scheduler.getContext();
	}

	/* (non-Javadoc)
	 * @see org.quartz.Scheduler#start()
	 */
	@Override
	public final void start() throws SchedulerException {
		this.scheduler.start();
	}

	/* (non-Javadoc)
	 * @see org.quartz.Scheduler#startDelayed(int)
	 */
	@Override
	public final void startDelayed(int seconds) throws SchedulerException {
		this.scheduler.startDelayed(seconds);
	}

	/* (non-Javadoc)
	 * @see org.quartz.Scheduler#isStarted()
	 */
	@Override
	public final boolean isStarted() throws SchedulerException {
		return this.scheduler.isStarted();
	}

	/* (non-Javadoc)
	 * @see org.quartz.Scheduler#standby()
	 */
	@Override
	public final void standby() throws SchedulerException {
		this.scheduler.standby();
	}

	/* (non-Javadoc)
	 * @see org.quartz.Scheduler#isInStandbyMode()
	 */
	@Override
	public final boolean isInStandbyMode() throws SchedulerException {
		return this.scheduler.isInStandbyMode();
	}

	/* (non-Javadoc)
	 * @see org.quartz.Scheduler#shutdown()
	 */
	@Override
	public final void shutdown() throws SchedulerException {
		stop();
		this.scheduler.shutdown();
	}

	/* (non-Javadoc)
	 * @see org.quartz.Scheduler#shutdown(boolean)
	 */
	@Override
	public final void shutdown(boolean waitForJobsToComplete)
			throws SchedulerException {
		stop();
		this.scheduler.shutdown(waitForJobsToComplete);
	}

	/* (non-Javadoc)
	 * @see org.quartz.Scheduler#isShutdown()
	 */
	@Override
	public final boolean isShutdown() throws SchedulerException {
		return this.scheduler.isShutdown();
	}

	/* (non-Javadoc)
	 * @see org.quartz.Scheduler#getMetaData()
	 */
	@Override
	public final SchedulerMetaData getMetaData() throws SchedulerException {
		return this.scheduler.getMetaData();
	}

	/* (non-Javadoc)
	 * @see org.quartz.Scheduler#getCurrentlyExecutingJobs()
	 */
	@Override
	public final List<JobExecutionContext> getCurrentlyExecutingJobs()
			throws SchedulerException {
		return this.scheduler.getCurrentlyExecutingJobs();
	}

	/* (non-Javadoc)
	 * @see org.quartz.Scheduler#setJobFactory(org.quartz.spi.JobFactory)
	 */
	@Override
	public final void setJobFactory(JobFactory factory) throws SchedulerException {
		this.scheduler.setJobFactory(factory);
	}

	/* (non-Javadoc)
	 * @see org.quartz.Scheduler#getListenerManager()
	 */
	@Override
	public final ListenerManager getListenerManager() throws SchedulerException {
		return this.scheduler.getListenerManager();
	}

	/* (non-Javadoc)
	 * @see org.quartz.Scheduler#unscheduleJob(org.quartz.TriggerKey)
	 */
	@Override
	public final boolean unscheduleJob(TriggerKey triggerKey)
			throws SchedulerException {
		return this.scheduler.unscheduleJob(triggerKey);
	}

	/* (non-Javadoc)
	 * @see org.quartz.Scheduler#unscheduleJobs(java.util.List)
	 */
	@Override
	public final boolean unscheduleJobs(List<TriggerKey> triggerKeys)
			throws SchedulerException {
		return this.scheduler.unscheduleJobs(triggerKeys);
	}

	/* (non-Javadoc)
	 * @see org.quartz.Scheduler#addJob(org.quartz.JobDetail, boolean)
	 */
	@Override
	public final void addJob(JobDetail jobDetail, boolean replace)
			throws SchedulerException {
		this.scheduler.addJob(jobDetail, replace);
	}

	/* (non-Javadoc)
	 * @see org.quartz.Scheduler#deleteJob(org.quartz.JobKey)
	 */
	@Override
	public final boolean deleteJob(JobKey jobKey) throws SchedulerException {
		return this.scheduler.deleteJob(jobKey);
	}

	/* (non-Javadoc)
	 * @see org.quartz.Scheduler#deleteJobs(java.util.List)
	 */
	@Override
	public final boolean deleteJobs(List<JobKey> jobKeys) throws SchedulerException {
		return this.scheduler.deleteJobs(jobKeys);
	}

	/* (non-Javadoc)
	 * @see org.quartz.Scheduler#triggerJob(org.quartz.JobKey)
	 */
	@Override
	public final void triggerJob(JobKey jobKey) throws SchedulerException {
		this.scheduler.triggerJob(jobKey);
	}

	/* (non-Javadoc)
	 * @see org.quartz.Scheduler#triggerJob(org.quartz.JobKey, org.quartz.JobDataMap)
	 */
	@Override
	public final void triggerJob(JobKey jobKey, JobDataMap data)
			throws SchedulerException {
		this.scheduler.triggerJob(jobKey, data);
	}

	/* (non-Javadoc)
	 * @see org.quartz.Scheduler#pauseJob(org.quartz.JobKey)
	 */
	@Override
	public final void pauseJob(JobKey jobKey) throws SchedulerException {
		this.scheduler.pauseJob(jobKey);
	}

	/* (non-Javadoc)
	 * @see org.quartz.Scheduler#pauseJobs(org.quartz.impl.matchers.GroupMatcher)
	 */
	@Override
	public final void pauseJobs(GroupMatcher<JobKey> matcher)
			throws SchedulerException {
		this.scheduler.pauseJobs(matcher);
	}

	/* (non-Javadoc)
	 * @see org.quartz.Scheduler#pauseTrigger(org.quartz.TriggerKey)
	 */
	@Override
	public final void pauseTrigger(TriggerKey triggerKey) throws SchedulerException {
		this.scheduler.pauseTrigger(triggerKey);
	}

	/* (non-Javadoc)
	 * @see org.quartz.Scheduler#pauseTriggers(org.quartz.impl.matchers.GroupMatcher)
	 */
	@Override
	public final void pauseTriggers(GroupMatcher<TriggerKey> matcher)
			throws SchedulerException {
		this.scheduler.pauseTriggers(matcher);
	}

	/* (non-Javadoc)
	 * @see org.quartz.Scheduler#resumeJob(org.quartz.JobKey)
	 */
	@Override
	public final void resumeJob(JobKey jobKey) throws SchedulerException {
		this.scheduler.resumeJob(jobKey);
	}

	/* (non-Javadoc)
	 * @see org.quartz.Scheduler#resumeJobs(org.quartz.impl.matchers.GroupMatcher)
	 */
	@Override
	public final void resumeJobs(GroupMatcher<JobKey> matcher)
			throws SchedulerException {
		this.scheduler.resumeJobs(matcher);
	}

	/* (non-Javadoc)
	 * @see org.quartz.Scheduler#resumeTrigger(org.quartz.TriggerKey)
	 */
	@Override
	public final void resumeTrigger(TriggerKey triggerKey) throws SchedulerException {
		this.scheduler.resumeTrigger(triggerKey);
	}

	/* (non-Javadoc)
	 * @see org.quartz.Scheduler#resumeTriggers(org.quartz.impl.matchers.GroupMatcher)
	 */
	@Override
	public final void resumeTriggers(GroupMatcher<TriggerKey> matcher)
			throws SchedulerException {
		this.scheduler.resumeTriggers(matcher);
	}

	/* (non-Javadoc)
	 * @see org.quartz.Scheduler#pauseAll()
	 */
	@Override
	public final void pauseAll() throws SchedulerException {
		this.scheduler.pauseAll();
	}

	/* (non-Javadoc)
	 * @see org.quartz.Scheduler#resumeAll()
	 */
	@Override
	public final void resumeAll() throws SchedulerException {
		this.scheduler.resumeAll();
	}

	/* (non-Javadoc)
	 * @see org.quartz.Scheduler#getJobGroupNames()
	 */
	@Override
	public final List<String> getJobGroupNames() throws SchedulerException {
		return this.scheduler.getJobGroupNames();
	}

	/* (non-Javadoc)
	 * @see org.quartz.Scheduler#getJobKeys(org.quartz.impl.matchers.GroupMatcher)
	 */
	@Override
	public final Set<JobKey> getJobKeys(GroupMatcher<JobKey> matcher)
			throws SchedulerException {
		return this.scheduler.getJobKeys(matcher);
	}

	/* (non-Javadoc)
	 * @see org.quartz.Scheduler#getTriggerGroupNames()
	 */
	@Override
	public final List<String> getTriggerGroupNames() throws SchedulerException {
		return this.scheduler.getTriggerGroupNames();
	}

	/* (non-Javadoc)
	 * @see org.quartz.Scheduler#getTriggerKeys(org.quartz.impl.matchers.GroupMatcher)
	 */
	@Override
	public final Set<TriggerKey> getTriggerKeys(GroupMatcher<TriggerKey> matcher)
			throws SchedulerException {
		return this.scheduler.getTriggerKeys(matcher);
	}

	/* (non-Javadoc)
	 * @see org.quartz.Scheduler#getPausedTriggerGroups()
	 */
	@Override
	public final Set<String> getPausedTriggerGroups() throws SchedulerException {
		return this.scheduler.getPausedTriggerGroups();
	}

	/* (non-Javadoc)
	 * @see org.quartz.Scheduler#getJobDetail(org.quartz.JobKey)
	 */
	@Override
	public final JobDetail getJobDetail(JobKey jobKey) throws SchedulerException {
		return this.scheduler.getJobDetail(jobKey);
	}

	/* (non-Javadoc)
	 * @see org.quartz.Scheduler#getTriggerState(org.quartz.TriggerKey)
	 */
	@Override
	public final TriggerState getTriggerState(TriggerKey triggerKey)
			throws SchedulerException {
		return this.scheduler.getTriggerState(triggerKey);
	}

	/* (non-Javadoc)
	 * @see org.quartz.Scheduler#deleteCalendar(java.lang.String)
	 */
	@Override
	public final boolean deleteCalendar(String calName) throws SchedulerException {
		return this.scheduler.deleteCalendar(calName);
	}

	/* (non-Javadoc)
	 * @see org.quartz.Scheduler#getCalendarNames()
	 */
	@Override
	public final List<String> getCalendarNames() throws SchedulerException {
		return this.scheduler.getCalendarNames();
	}

	/* (non-Javadoc)
	 * @see org.quartz.Scheduler#interrupt(org.quartz.JobKey)
	 */
	@Override
	public final boolean interrupt(JobKey jobKey)
			throws UnableToInterruptJobException {
		return this.scheduler.interrupt(jobKey);
	}

	/* (non-Javadoc)
	 * @see org.quartz.Scheduler#interrupt(java.lang.String)
	 */
	@Override
	public final boolean interrupt(String fireInstanceId)
			throws UnableToInterruptJobException {
		return this.scheduler.interrupt(fireInstanceId);
	}

	/* (non-Javadoc)
	 * @see org.quartz.Scheduler#checkExists(org.quartz.JobKey)
	 */
	@Override
	public final boolean checkExists(JobKey jobKey) throws SchedulerException {
		return this.scheduler.checkExists(jobKey);
	}

	/* (non-Javadoc)
	 * @see org.quartz.Scheduler#checkExists(org.quartz.TriggerKey)
	 */
	@Override
	public final boolean checkExists(TriggerKey triggerKey) throws SchedulerException {
		return this.scheduler.checkExists(triggerKey);
	}

	/* (non-Javadoc)
	 * @see org.quartz.Scheduler#clear()
	 */
	@Override
	public final void clear() throws SchedulerException {
		this.scheduler.clear();
	}

	/**
	 * @param scheduler
	 */
	public final void setScheduler(Scheduler scheduler) {
		this.scheduler = scheduler;
	}

	/* (non-Javadoc)
	 * @see org.quartz.Scheduler#scheduleJob(org.quartz.JobDetail, org.quartz.Trigger)
	 */
	@Override
	public final Date scheduleJob(JobDetail jobDetail, Trigger trigger)
			throws SchedulerException {
		return this.scheduler.scheduleJob(jobDetail, trigger);
	}

	/* (non-Javadoc)
	 * @see org.quartz.Scheduler#scheduleJob(org.quartz.Trigger)
	 */
	@Override
	public final Date scheduleJob(Trigger trigger) throws SchedulerException {
		return this.scheduler.scheduleJob(trigger);
	}

	/* (non-Javadoc)
	 * @see org.quartz.Scheduler#scheduleJobs(java.util.Map, boolean)
	 */
	@Override
	public final void scheduleJobs(Map<JobDetail, List<Trigger>> triggersAndJobs,
			boolean replace) throws SchedulerException {
		this.scheduler.scheduleJobs(triggersAndJobs, replace);
	}

	/* (non-Javadoc)
	 * @see org.quartz.Scheduler#rescheduleJob(org.quartz.TriggerKey, org.quartz.Trigger)
	 */
	@Override
	public final Date rescheduleJob(TriggerKey triggerKey, Trigger newTrigger)
			throws SchedulerException {
		return this.scheduler.rescheduleJob(triggerKey, newTrigger);
	}

	/* (non-Javadoc)
	 * @see org.quartz.Scheduler#getTriggersOfJob(org.quartz.JobKey)
	 */
	@Override
	public final List<? extends Trigger> getTriggersOfJob(JobKey jobKey)
			throws SchedulerException {
		return this.scheduler.getTriggersOfJob(jobKey);
	}

	/* (non-Javadoc)
	 * @see org.quartz.Scheduler#getTrigger(org.quartz.TriggerKey)
	 */
	@Override
	public final Trigger getTrigger(TriggerKey triggerKey) throws SchedulerException {
		return this.scheduler.getTrigger(triggerKey);
	}

	/* (non-Javadoc)
	 * @see org.quartz.Scheduler#addCalendar(java.lang.String, org.quartz.Calendar, boolean, boolean)
	 */
	@Override
	public final void addCalendar(String calName, org.quartz.Calendar calendar,
			boolean replace, boolean updateTriggers) throws SchedulerException {
		this.scheduler.addCalendar(calName, calendar, replace, updateTriggers);
	}

	/* (non-Javadoc)
	 * @see org.quartz.Scheduler#getCalendar(java.lang.String)
	 */
	@Override
	public final org.quartz.Calendar getCalendar(String calName)
			throws SchedulerException {
		return this.scheduler.getCalendar(calName);
	}
}
