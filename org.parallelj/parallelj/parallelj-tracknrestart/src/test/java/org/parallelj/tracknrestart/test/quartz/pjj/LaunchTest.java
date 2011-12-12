package org.parallelj.tracknrestart.test.quartz.pjj;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.parallelj.internal.reflect.ProgramAdapter.Adapter;
import org.parallelj.tracknrestart.plugins.TrackNRestartPlugin;
import org.parallelj.tracknrestart.util.TrackNRestartLoader;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LaunchTest {

	static Logger log = LoggerFactory.getLogger(LaunchTest.class);

	
	public static void main(String[] args) {
		LaunchTest lt = new LaunchTest();
		lt.test1();
	}

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {

		TrackNRestartLoader.cleanTrackingDatabase("scripts/quartz-database-init-mysql.sql", "database.properties");

		TrackNRestartLoader.cleanTrackingDatabase("scripts/quartz-track-database-init-mysql.sql", "database.properties");

	}

	@Before
	public void setUp() throws Exception {
		log.info("");
		log.info("///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////:");
		log.info("");
	}

	/**
	 * First launch
	 */
	@Test
	public void test1() {
		
		String restartId = null;
		String programQN = "org.parallelj.tracknrestart.test.quartz.pjj.flow.Prog1";
		String jobName = "JJP_TestJob1";
		String groupName = "DEFAULT";
		String triggerName = "JJP_TestTrigger1";
		List<String> params = Arrays.asList(new String[]{"a","b","c"});

		try {
			SchedulerFactory sf = new StdSchedulerFactory("quartz.properties");

			Scheduler sched = sf.getScheduler();
			
			sched.start();

			Class<? extends Adapter> jobClass = ((Class<? extends Adapter>) Class.forName(programQN));
			JobBuilder jobBuilder = newJob((Class<? extends Job>) jobClass);

			JobDataMap jobDataMap = new JobDataMap();
			jobDataMap.put("data1",params);

			JobDetail job = createJob(groupName, jobName, restartId, jobBuilder, jobDataMap);
			
			Trigger trigger = newTrigger()
					.withIdentity(triggerName, groupName)
					.startNow()
					.build();
			
			sched.scheduleJob(job, trigger);
			// wait 5 seconds to give our jobs a chance to run
			try {
				Thread.sleep(5L * 1000L);
			} catch (Exception e) {
			}

			sched.shutdown(true);
		} catch (SchedulerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Restart launch from test1
	 */
	@Test
	public void test2() {
		String restartId = "_LAST_";
		String programQN = "org.parallelj.tracknrestart.test.quartz.pjj.flow.Prog1";
		String jobName = "JJP_TestJob1";
		String groupName = "DEFAULT";
		String triggerName = "JJP_TestTrigger1";
		List<String> params = Arrays.asList(new String[]{"a","b","c"});

		try {
			SchedulerFactory sf = new StdSchedulerFactory("quartz.properties");

			Scheduler sched = sf.getScheduler();
			
			sched.start();

			Class<? extends Adapter> jobClass = ((Class<? extends Adapter>) Class.forName(programQN));
			JobBuilder jobBuilder = newJob((Class<? extends Job>) jobClass);

			JobDataMap jobDataMap = new JobDataMap();
			jobDataMap.put("data1",params);

			JobDetail job = createJob(groupName, jobName, restartId, jobBuilder, jobDataMap);

			Trigger trigger = newTrigger()
					.withIdentity(triggerName, groupName)
					.startNow()
					.build();
			
				sched.scheduleJob(job, trigger);
			// wait 5 seconds to give our jobs a chance to run
			try {
				Thread.sleep(5L * 1000L);
			} catch (Exception e) {
			}

			sched.shutdown(true);
		} catch (SchedulerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * "a" and "c" persisted with 'success', "B" and "0" persisted with 'failure' 
	 */
	@Test
	public void test3() {
		
		String restartId = null;
		String programQN = "org.parallelj.tracknrestart.test.quartz.pjj.flow.Prog1";
		String jobName = "JJP_TestJob2";
		String groupName = "DEFAULT";
		String triggerName = "JJP_TestTrigger2";
		List<String> params = Arrays.asList(new String[]{"a","B","c","0"});

		try {
			SchedulerFactory sf = new StdSchedulerFactory("quartz.properties");

			Scheduler sched = sf.getScheduler();
			
			sched.start();

			Class<? extends Adapter> jobClass = ((Class<? extends Adapter>) Class.forName(programQN));
			JobBuilder jobBuilder = newJob((Class<? extends Job>) jobClass);

			JobDataMap jobDataMap = new JobDataMap();
			jobDataMap.put("data1",params);
			
			JobDetail job = createJob(groupName, jobName, restartId, jobBuilder, jobDataMap);

			Trigger trigger = newTrigger()
					.withIdentity(triggerName, groupName)
					.startNow()
					.build();
			
			sched.scheduleJob(job, trigger);
			// wait 5 seconds to give our jobs a chance to run
			try {
				Thread.sleep(5L * 1000L);
			} catch (Exception e) {
			}

			sched.shutdown(true);
		} catch (SchedulerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * second "a" leads to duplicate key then flow is aborted 
	 */
	@Test
	public void test4() {
		
		String restartId = null;
		String programQN = "org.parallelj.tracknrestart.test.quartz.pjj.flow.Prog1";
		String jobName = "JJP_TestJob3";
		String groupName = "DEFAULT";
		String triggerName = "JJP_TestTrigger3";
		List<String> params = Arrays.asList(new String[]{"a","b","a"});

		try {
			SchedulerFactory sf = new StdSchedulerFactory("quartz.properties");

			Scheduler sched = sf.getScheduler();
			
			sched.start();

			Class<? extends Adapter> jobClass = ((Class<? extends Adapter>) Class.forName(programQN));
			JobBuilder jobBuilder = newJob((Class<? extends Job>) jobClass);

			JobDataMap jobDataMap = new JobDataMap();
			jobDataMap.put("data1",params);
			
			JobDetail job = createJob(groupName, jobName, restartId, jobBuilder, jobDataMap);

			Trigger trigger = newTrigger()
					.withIdentity(triggerName, groupName)
					.startNow()
					.build();
			
			sched.scheduleJob(job, trigger);
			// wait 5 seconds to give our jobs a chance to run
			try {
				Thread.sleep(5L * 1000L);
			} catch (Exception e) {
			}

			sched.shutdown(true);
		} catch (SchedulerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private JobDetail createJob(String groupName, String jobName, String restartId, JobBuilder jobBuilder, JobDataMap jobDataMap) {
		JobDetail job;
		if (restartId==null) {
			job = jobBuilder
					.withIdentity(jobName, groupName)
					.usingJobData(jobDataMap)
					.build();
		} else {
			job = jobBuilder
					.withIdentity(jobName, groupName)
					.usingJobData(TrackNRestartPlugin.RESTARTED_FIRE_INSTANCE_ID, restartId)
					.usingJobData(jobDataMap)
					.build();
		}
		return job;
	}

}
