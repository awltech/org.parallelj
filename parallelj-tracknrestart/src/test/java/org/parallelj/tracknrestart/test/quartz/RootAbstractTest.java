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
package org.parallelj.tracknrestart.test.quartz;

import static org.junit.Assert.fail;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import junit.framework.Assert;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.parallelj.internal.reflect.ProgramAdapter.Adapter;
import org.parallelj.launching.quartz.ParalleljSchedulerFactory;
import org.parallelj.launching.quartz.ParalleljSchedulerRepository;
import org.parallelj.tracknrestart.ReturnCodes;
import org.parallelj.tracknrestart.aspects.QuartzContextAdapter;
import org.parallelj.tracknrestart.plugins.TrackNRestartPluginAll;
import org.parallelj.tracknrestart.test.quartz.alone.TestListener;
import org.parallelj.tracknrestart.test.quartz.pjj.flow.runnable.People;
import org.parallelj.tracknrestart.util.TrackNRestartLoader;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.Trigger;
import org.quartz.impl.matchers.EverythingMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class defines several tests, with setup database, preparing data to be processed and checking result.
 * This class must be extended to indicate which //J Program must be called by the tests.
 * 
 * Class to be extended to launch the embedded tests matrix
 *
 */
public abstract class RootAbstractTest {
	
	protected static final String DB = "hsqldb"; //"mysql"

	protected static Logger log = LoggerFactory.getLogger(RootAbstractTest.class);

	protected static Scheduler sched = null;
	
	protected static TestListener jl = null;

	protected static boolean DBinitializationDone = false;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {

		if (!DBinitializationDone) {
			TrackNRestartLoader.cleanTrackingDatabase(
					"scripts/quartz-database-init-"+DB+".sql",
					"database."+DB+".properties");
			TrackNRestartLoader.cleanTrackingDatabase(
					"scripts/quartz-track-database-init-"+DB+".sql",
					"database."+DB+".properties");
		}
		SchedulerFactory sf = new ParalleljSchedulerFactory("quartz."+DB+".properties");
		sched = sf.getScheduler();
		sched.start();

		// wait 5 seconds to give our jobs a chance to run
		try {
			Thread.sleep(5L * 1000L);
		} catch (Exception e) {
		}

		jl = new TestListener("TestListener", sched);
		try {
			sched.getListenerManager().addJobListener(jl,
					EverythingMatcher.allJobs());
			sched.getListenerManager().addSchedulerListener(jl);
		} catch (SchedulerException e2) {
			fail();
			return;
		}
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		sched.getListenerManager().removeJobListener(jl.getName());
		sched.getListenerManager().removeSchedulerListener(jl);
		sched.shutdown(true);
		ParalleljSchedulerRepository.getInstance().remove(sched.getSchedulerName());
	}

	@Before
	public void setUp() throws Exception {
		log.info("");
		log.info("///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////:");
		log.info("");
	}
	
	protected JobDetail createJob(String groupName, String jobName, String restartId, JobBuilder jobBuilder, JobDataMap jobDataMap) {
		JobDetail job;
		if (restartId==null) {
			job = jobBuilder
					.withIdentity(jobName, groupName)
					.usingJobData(jobDataMap)
					.build();
		} else {
			job = jobBuilder
					.withIdentity(jobName, groupName)
					.usingJobData(TrackNRestartPluginAll.RESTARTED_FIRE_INSTANCE_ID, restartId)
					.usingJobData(jobDataMap)
					.build();
		}
		return job;
	}

	protected Trigger createTrigger(String groupName, String triggerName) {
		Trigger trigger = newTrigger()
				.withIdentity(triggerName, groupName)
				.startNow()
				.build();
		return trigger;
	}

	protected CountDownLatch createLatcher(TestListener jl) {
		CountDownLatch latcher = new CountDownLatch(1);
		jl.setLatcher(latcher);
		return latcher;
	}

	protected CountDownLatch createLatcher(TestListener jl, int n) {
		CountDownLatch latcher = new CountDownLatch(n);
		jl.setLatcher(latcher);
		return latcher;
	}

	protected void awaitingLatcher(CountDownLatch latcher, TestListener jl) {
		try {
			if (latcher.getCount()>0) {
				log.info("***********************AWAIT********************************"+latcher.getCount());
				latcher.await();
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		jl.setLatcher(null);
	}
}
