package org.parallelj.tracknrestart.test.quartz.alone;

import static org.junit.Assert.*;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;
import junit.framework.Assert;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.parallelj.tracknrestart.plugins.TrackNRestartPlugin;
import org.parallelj.tracknrestart.util.TrackNRestartLoader;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.matchers.EverythingMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AllTest {

	static Scheduler sched = null;
	
	static TestListener jl = null;

	static Logger log = LoggerFactory.getLogger(AllTest.class);

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {

		TrackNRestartLoader.cleanTrackingDatabase("scripts/quartz-database-init-mysql.sql", "database.properties");

		TrackNRestartLoader.cleanTrackingDatabase("scripts/quartz-track-database-init-mysql.sql", "database.properties");

		SchedulerFactory sf = new StdSchedulerFactory("quartz.properties");

		sched = sf.getScheduler();
		
		sched.start();

		// wait 5 seconds to give our jobs a chance to run
		try {
			Thread.sleep(5L * 1000L);
		} catch (Exception e) {
		}
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		sched.shutdown(true);
	}

	@Before
	public void setUp() throws Exception {
		log.info("------------------------------------------------------------------------------------------------------------------------------");
		jl = new TestListener("TestListener", sched);
		sched.getListenerManager().addJobListener(jl,
				EverythingMatcher.allJobs());
	}

	@After
	public void tearDown() throws Exception {
		sched.getListenerManager().removeJobListener(jl.getName());
	}

	//@Test(timeout=10000)
	public void test1() {
		JobDetail job = newJob(SimpleJob.class)
				.withIdentity("TestJob1","DEFAULT")
				.build();

		Trigger trigger = newTrigger()
				.withIdentity("TestTrigger1", "DEFAULT")
				.startNow()
				.build();

		
		try {
			sched.scheduleJob(job, trigger);
			try {
				Thread.sleep(5L * 1000L);
			} catch (Exception e) {
			}

			JobDataMap result = jl.getResult();
			int iterationNumber = result.getInt("nbrExpected");
			log.info("nbrExpected="+iterationNumber);
			int nbrSuccess = result.getInt("nbrSuccess");
			log.info("nbrSuccess="+nbrSuccess);
			int nbrFailure = result.getInt("nbrFailure");
			log.info("nbrFailure="+nbrFailure);
			String rc = result.getString("RETURN_CODE");
			log.info("rc="+rc);
			Assert.assertEquals(iterationNumber, nbrSuccess+nbrFailure);

		} catch (SchedulerException e) {
			fail(e.getMessage());
		}
	}

	//@Test(timeout=100000)
	public void test2() {
		JobDetail job = newJob(SimpleJob.class)
				.withIdentity("TestJob1","DEFAULT")
				.usingJobData(TrackNRestartPlugin.RESTARTED_FIRE_INSTANCE_ID, "UNKNOWN_ID")
				.build();

		Trigger trigger = newTrigger()
				.withIdentity("TestTrigger1", "DEFAULT")
				.startNow()
				.build();

		
		try {
			sched.scheduleJob(job, trigger);
			try {
				Thread.sleep(5L * 1000L);
			} catch (Exception e) {
			}
			assertNull(jl.getResult());
		} catch (ClassCastException e) {
			throw e;
		} catch (SchedulerException e) {
			fail(e.getMessage());
		}
	}

	//@Test(timeout=100000)
	public void test3() {
		JobDetail job = newJob(SimpleJob.class)
				.withIdentity("NO_HISTORY_JOB","DEFAULT")
				.usingJobData(TrackNRestartPlugin.RESTARTED_FIRE_INSTANCE_ID, "_LAST_")
				.build();

		Trigger trigger = newTrigger()
				.withIdentity("TestTrigger1", "DEFAULT")
				.startNow()
				.build();

		
		try {
			sched.scheduleJob(job, trigger);
			try {
				Thread.sleep(5L * 1000L);
			} catch (Exception e) {
			}
			assertNull(jl.getResult());
		} catch (ClassCastException e) {
			throw e;
		} catch (SchedulerException e) {
			fail(e.getMessage());
		}
	}

	//@Test(timeout=200000)
	public void test4() {
		String rc = null;
		int totalSuccess = 0;
		int iterationNumber = 0;
		String oldCurrentFireInstanceId = null;
		do {
			JobDetail job = null;
			Trigger trigger = null;
			if (rc==null) {
					job = newJob(SimpleJob.class)
							.withIdentity("TestJob2", "DEFAULT")
							.build();
					trigger = newTrigger()
							.withIdentity("TestTrigger2", "DEFAULT").startNow().build();
				} else {
					job = newJob(SimpleJob.class)
							.withIdentity("TestJob2", "DEFAULT")
							.usingJobData(
									TrackNRestartPlugin.RESTARTED_FIRE_INSTANCE_ID,
									"_LAST_").build();
					trigger = newTrigger()
							.withIdentity("TestTrigger2", "DEFAULT").startNow().build();
				}
			try {
				sched.scheduleJob(job, trigger);
				try {
					Thread.sleep(5L * 1000L);
				} catch (Exception e) {
				}
				JobDataMap result = jl.getResult();
				iterationNumber = result.getInt("nbrExpected");
				log.info("nbrExpected=" + iterationNumber);
				int nbrSuccess = result.getInt("nbrSuccess");
				log.info("nbrSuccess=" + nbrSuccess);
				int nbrFailure = result.getInt("nbrFailure");
				log.info("nbrFailure=" + nbrFailure);
				rc = result.getString("RETURN_CODE");
				log.info("rc=" + rc);
				String restartedFireInstanceId = result.getString(TrackNRestartPlugin.RESTARTED_FIRE_INSTANCE_ID);
				log.info(TrackNRestartPlugin.RESTARTED_FIRE_INSTANCE_ID+"=" + restartedFireInstanceId);
				String currentFireInstanceId = result.getString("currentFireInstanceId");
				log.info("currentFireInstanceId=" + currentFireInstanceId);
				totalSuccess=totalSuccess+nbrSuccess;
				log.info("totalSuccess=" + totalSuccess);
				Assert.assertTrue(iterationNumber >= nbrSuccess + nbrFailure);
				Assert.assertEquals(restartedFireInstanceId, oldCurrentFireInstanceId);
				oldCurrentFireInstanceId = currentFireInstanceId;

			} catch (SchedulerException e) {
				fail(e.getMessage());
			}
			
			try {
				tearDown();
				setUp();
			} catch (Exception e) {
				fail();
			}
			
		} while (rc.equals("FAILURE"));
		Assert.assertNotSame("ABORTED", rc);
		Assert.assertEquals(iterationNumber, totalSuccess);
	}

}
