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

import static org.quartz.TriggerBuilder.newTrigger;

import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Matcher;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.matchers.KeyMatcher;


public class AdapterJobsRunner {
	public static synchronized Object syncLaunch(Scheduler scheduler,
			JobDetail job) throws SchedulerException {
		// Trigger the job to run on the next round minute
		TriggerBuilder<Trigger> triggerBuilder = newTrigger();
		Trigger trigger = triggerBuilder
				.withIdentity(String.valueOf(triggerBuilder),
						String.valueOf(triggerBuilder)).startNow().build();

		// Define a listener to wait until the Job is completed
		AdapterJobListener listener = null;
		listener = new AdapterJobListener();
		Matcher<JobKey> matcher = KeyMatcher.keyEquals(job.getKey());
		scheduler.getListenerManager().addJobListener(listener, matcher);

		// Launch the Job
		scheduler.scheduleJob(job, trigger);
		scheduler.start();

		// Waiting for the job completed...?
		while (!listener.isJobTerminated()) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
			}
		}
		// Initialize the result of the Job
		Object result = listener.getResult();
		scheduler.getListenerManager().removeJobListener(listener.getName());
		return result;
	}

	public static void ayncLaunch(ParalleljScheduler scheduler, JobDetail job)
			throws SchedulerException {
		// Trigger the job to run on the next round minute
		TriggerBuilder<Trigger> triggerBuilder = newTrigger();
		Trigger trigger = triggerBuilder
				.withIdentity(String.valueOf(triggerBuilder),
						String.valueOf(triggerBuilder)).startNow().build();

		// Launch the Job
		scheduler.scheduleJob(job, trigger);
		scheduler.start();
	}
}
