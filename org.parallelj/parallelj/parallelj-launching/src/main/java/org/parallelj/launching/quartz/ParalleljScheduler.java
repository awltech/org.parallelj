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
 * This class is a Parallelj implementation of a Quartz Scheduler. 
 * It allows to enrich default Quartz Scheduler 
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
		// TODO 
		
		// Load Parallelj Configuration file
		this.configuration = ParalleljConfigurationManager.getConfiguration();
		
		// And others...
		
	}

	@Override
	public String getSchedulerName() throws SchedulerException {
		return scheduler.getSchedulerName();
	}

	@Override
	public String getSchedulerInstanceId() throws SchedulerException {
		return scheduler.getSchedulerInstanceId();
	}

	@Override
	public SchedulerContext getContext() throws SchedulerException {
		return scheduler.getContext();
	}

	@Override
	public void start() throws SchedulerException {
		scheduler.start();
	}

	@Override
	public void startDelayed(int seconds) throws SchedulerException {
		scheduler.startDelayed(seconds);
	}

	@Override
	public boolean isStarted() throws SchedulerException {
		return scheduler.isStarted();
	}

	@Override
	public void standby() throws SchedulerException {
		scheduler.standby();
	}

	@Override
	public boolean isInStandbyMode() throws SchedulerException {
		return scheduler.isInStandbyMode();
	}

	@Override
	public void shutdown() throws SchedulerException {
		scheduler.shutdown();
	}

	@Override
	public void shutdown(boolean waitForJobsToComplete)
			throws SchedulerException {
		scheduler.shutdown(waitForJobsToComplete);
	}

	@Override
	public boolean isShutdown() throws SchedulerException {
		return scheduler.isShutdown();
	}

	@Override
	public SchedulerMetaData getMetaData() throws SchedulerException {
		return scheduler.getMetaData();
	}

	@Override
	public List<JobExecutionContext> getCurrentlyExecutingJobs()
			throws SchedulerException {
		return scheduler.getCurrentlyExecutingJobs();
	}

	@Override
	public void setJobFactory(JobFactory factory) throws SchedulerException {
		scheduler.setJobFactory(factory);
	}

	@Override
	public ListenerManager getListenerManager() throws SchedulerException {
		return scheduler.getListenerManager();
	}

	@Override
	public boolean unscheduleJob(TriggerKey triggerKey)
			throws SchedulerException {
		return scheduler.unscheduleJob(triggerKey);
	}

	@Override
	public boolean unscheduleJobs(List<TriggerKey> triggerKeys)
			throws SchedulerException {
		return scheduler.unscheduleJobs(triggerKeys);
	}

	@Override
	public void addJob(JobDetail jobDetail, boolean replace)
			throws SchedulerException {
		scheduler.addJob(jobDetail, replace);
	}

	@Override
	public boolean deleteJob(JobKey jobKey) throws SchedulerException {
		return scheduler.deleteJob(jobKey);
	}

	@Override
	public boolean deleteJobs(List<JobKey> jobKeys) throws SchedulerException {
		return scheduler.deleteJobs(jobKeys);
	}

	@Override
	public void triggerJob(JobKey jobKey) throws SchedulerException {
		scheduler.triggerJob(jobKey);		
	}

	@Override
	public void triggerJob(JobKey jobKey, JobDataMap data)
			throws SchedulerException {
		scheduler.triggerJob(jobKey, data);
	}

	@Override
	public void pauseJob(JobKey jobKey) throws SchedulerException {
		scheduler.pauseJob(jobKey);
	}

	@Override
	public void pauseJobs(GroupMatcher<JobKey> matcher)
			throws SchedulerException {
		scheduler.pauseJobs(matcher);
	}

	@Override
	public void pauseTrigger(TriggerKey triggerKey) throws SchedulerException {
		scheduler.pauseTrigger(triggerKey);
	}

	@Override
	public void pauseTriggers(GroupMatcher<TriggerKey> matcher)
			throws SchedulerException {
		scheduler.pauseTriggers(matcher);
	}

	@Override
	public void resumeJob(JobKey jobKey) throws SchedulerException {
		scheduler.resumeJob(jobKey);
	}

	@Override
	public void resumeJobs(GroupMatcher<JobKey> matcher)
			throws SchedulerException {
		scheduler.resumeJobs(matcher);
	}

	@Override
	public void resumeTrigger(TriggerKey triggerKey) throws SchedulerException {
		scheduler.resumeTrigger(triggerKey);
	}

	@Override
	public void resumeTriggers(GroupMatcher<TriggerKey> matcher)
			throws SchedulerException {
		scheduler.resumeTriggers(matcher);
	}

	@Override
	public void pauseAll() throws SchedulerException {
		scheduler.pauseAll();
	}

	@Override
	public void resumeAll() throws SchedulerException {
		scheduler.resumeAll();
	}

	@Override
	public List<String> getJobGroupNames() throws SchedulerException {
		return scheduler.getJobGroupNames();
	}

	@Override
	public Set<JobKey> getJobKeys(GroupMatcher<JobKey> matcher)
			throws SchedulerException {
		return scheduler.getJobKeys(matcher);
	}

	@Override
	public List<String> getTriggerGroupNames() throws SchedulerException {
		return scheduler.getTriggerGroupNames();
	}

	@Override
	public Set<TriggerKey> getTriggerKeys(GroupMatcher<TriggerKey> matcher)
			throws SchedulerException {
		return scheduler.getTriggerKeys(matcher);
	}

	@Override
	public Set<String> getPausedTriggerGroups() throws SchedulerException {
		return scheduler.getPausedTriggerGroups();
	}

	@Override
	public JobDetail getJobDetail(JobKey jobKey) throws SchedulerException {
		return scheduler.getJobDetail(jobKey);
	}

	@Override
	public TriggerState getTriggerState(TriggerKey triggerKey)
			throws SchedulerException {
		return scheduler.getTriggerState(triggerKey);
	}

	@Override
	public boolean deleteCalendar(String calName) throws SchedulerException {
		return scheduler.deleteCalendar(calName);
	}

	@Override
	public List<String> getCalendarNames() throws SchedulerException {
		return scheduler.getCalendarNames();
	}

	@Override
	public boolean interrupt(JobKey jobKey)
			throws UnableToInterruptJobException {
		return scheduler.interrupt(jobKey);
	}

	@Override
	public boolean interrupt(String fireInstanceId)
			throws UnableToInterruptJobException {
		return scheduler.interrupt(fireInstanceId);
	}

	@Override
	public boolean checkExists(JobKey jobKey) throws SchedulerException {
		return scheduler.checkExists(jobKey);
	}

	@Override
	public boolean checkExists(TriggerKey triggerKey) throws SchedulerException {
		return scheduler.checkExists(triggerKey);
	}

	@Override
	public void clear() throws SchedulerException {
		scheduler.clear();
	}

	public void setScheduler(Scheduler scheduler) {
		this.scheduler = scheduler;
	}

	@Override
	public Date scheduleJob(JobDetail jobDetail, Trigger trigger)
			throws SchedulerException {
		return scheduler.scheduleJob(jobDetail, trigger);
	}

	@Override
	public Date scheduleJob(Trigger trigger) throws SchedulerException {
		return scheduler.scheduleJob(trigger);
	}

	@Override
	public void scheduleJobs(Map<JobDetail, List<Trigger>> triggersAndJobs,
			boolean replace) throws SchedulerException {
		scheduler.scheduleJobs(triggersAndJobs, replace);
	}

	@Override
	public Date rescheduleJob(TriggerKey triggerKey, Trigger newTrigger)
			throws SchedulerException {
		return scheduler.rescheduleJob(triggerKey, newTrigger);
	}

	@Override
	public List<? extends Trigger> getTriggersOfJob(JobKey jobKey)
			throws SchedulerException {
		return scheduler.getTriggersOfJob(jobKey);
	}

	@Override
	public Trigger getTrigger(TriggerKey triggerKey) throws SchedulerException {
		return scheduler.getTrigger(triggerKey);
	}

	@Override
	public void addCalendar(String calName, org.quartz.Calendar calendar,
			boolean replace, boolean updateTriggers) throws SchedulerException {
		scheduler.addCalendar(calName, calendar, replace, updateTriggers);
	}

	@Override
	public org.quartz.Calendar getCalendar(String calName)
			throws SchedulerException {
		return scheduler.getCalendar(calName);
	}

	/**
	 * 
	 * Getter method for the Configuration Object
	 * 
	 * @return the configuration
	 */
	public ParalleljConfiguration getConfiguration() {
		return configuration;
	}

}
