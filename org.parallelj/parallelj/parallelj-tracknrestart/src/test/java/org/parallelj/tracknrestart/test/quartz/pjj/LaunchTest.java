package org.parallelj.tracknrestart.test.quartz.pjj;

import static org.junit.Assert.fail;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.parallelj.internal.reflect.ProgramAdapter.Adapter;
import org.parallelj.tracknrestart.plugins.TrackNRestartPlugin;
import org.parallelj.tracknrestart.test.quartz.alone.TestListener;
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
import org.quartz.impl.matchers.EverythingMatcher;
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
			fail();
			return;
		} catch (ClassNotFoundException e) {
			fail();
			return;
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
			fail();
			return;
		} catch (ClassNotFoundException e) {
			fail();
			return;
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
			fail();
			return;
		} catch (ClassNotFoundException e) {
			fail();
			return;
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
			fail();
			return;
		} catch (ClassNotFoundException e) {
			fail();
			return;
		}
	}

	/**
	 * mass test 
	 */
	@Test
	public void test5() {
		
		String restartId = null;
		String programQN = "org.parallelj.tracknrestart.test.quartz.pjj.flow.Prog1";
		String groupName = "DEFAULT";

		try {
			SchedulerFactory sf = new StdSchedulerFactory("quartz.properties");

			Scheduler sched = sf.getScheduler();
			
			sched.start();

			int n = 100; 
			
			JobDetail[] job = new JobDetail[n];
			Trigger[] trigger = new Trigger[n];
			
			for (int i = 0; i < n; i++) {
				String triggerName = "JJP_TestTrigger_Concurrent"+i;
				String jobName = "JJP_TestJob_Concurrent"+i;
				String[] letters = {"a","b","c","d","e","f","g","h","i","j","k","l","m","n","o","p","q","r","s","t","u","v","w","x","y","z"};
				for (int j = 0; j < letters.length; j++) {
					letters[j]=letters[j]+i;
				}
				List<String> params = Arrays.asList(letters);
				Class<? extends Adapter> jobClass = ((Class<? extends Adapter>) Class.forName(programQN));
				JobBuilder jobBuilder = newJob((Class<? extends Job>) jobClass);
				JobDataMap jobDataMap = new JobDataMap();
				jobDataMap.put("data1",params);
				job[i] = createJob(groupName, jobName, restartId, jobBuilder, jobDataMap);
				trigger[i] = newTrigger()
						.withIdentity(triggerName, groupName)
						.startNow()
						.build();
			}
			
			for (int i = 0; i < n; i++) {
				sched.scheduleJob(job[i], trigger[i]);
			}
			
			// wait 5 seconds to give our jobs a chance to run
			try {
				Thread.sleep(5L * 1000L);
			} catch (Exception e) {
			}

			sched.shutdown(true);
		} catch (SchedulerException e) {
			fail();
			return;
		} catch (ClassNotFoundException e) {
			fail();
			return;
		}
	}

	/**
	 * mass test 
	 */
	@Test
	public void test6() {
		
		String restartId = null;
		String programQN = "org.parallelj.tracknrestart.test.quartz.pjj.flow.Prog1";
		String groupName = "DEFAULT";

			Scheduler sched = null;
			try {
				SchedulerFactory sf = new StdSchedulerFactory("quartz.properties");

				sched = sf.getScheduler();
				
				sched.start();
			} catch (SchedulerException e2) {
				fail();
				return;
			}

			// wait 5 seconds to give our jobs a chance to run
			try {
				Thread.sleep(5L * 1000L);
			} catch (Exception e) {
			}

			int n = 30; 
			
			String result = "FAILURE";
			
			for (int i = 0; i < n && "FAILURE".equals(result); i++) {
				TestListener jl = new TestListener("TestListener", sched);
				try {
					sched.getListenerManager().addJobListener(jl,
							EverythingMatcher.allJobs());
					sched.getListenerManager().addSchedulerListener(jl);
				} catch (SchedulerException e2) {
					fail();
					return;
				}

				CountDownLatch latcher = createLatcher(jl);
				
				String triggerName = "JJP_TestTrigger_Serialized";
				String jobName = "JJP_TestJob_Serialized";
				String[] letters = {"a","b","c","d","e","f","g","h","i","j","k","l","m","n","o","p","q","r","s","t","u","v","w","x","y","z"};
				List<String> params = Arrays.asList(letters);
				Class<? extends Adapter> jobClass = null;
				try {
					jobClass = ((Class<? extends Adapter>) Class.forName(programQN));
				} catch (ClassNotFoundException e2) {
					fail();
					return;
				}
				JobBuilder jobBuilder = newJob((Class<? extends Job>) jobClass);
				JobDataMap jobDataMap = new JobDataMap();
				jobDataMap.put("data1",params);
				if (i>0) {
					restartId = "_LAST_";
				}
				JobDetail job = createJob(groupName, jobName, restartId, jobBuilder, jobDataMap);
				Trigger trigger = newTrigger()
						.withIdentity(triggerName, groupName)
						.startNow()
						.build();
				
				try {
					sched.scheduleJob(job, trigger);
				} catch (SchedulerException e1) {
					e1.printStackTrace();
					fail();
					return;
				}

				try {
					Thread.sleep(5L * 1000L);
				} catch (Exception e) {
				}

				awaitingLatcher(latcher, jl);

				//tests
				
				try {
					sched.getListenerManager().removeJobListener(jl.getName());
				} catch (SchedulerException e) {
					fail();
					return;
				}

				result = (String)jl.getResult();

			}
			
			try {
				sched.shutdown(true);
			} catch (SchedulerException e) {
				fail();
				return;
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

	private CountDownLatch createLatcher(TestListener jl) {
		CountDownLatch latcher = new CountDownLatch(1);
		jl.setLatcher(latcher);
		return latcher;
	}

	private void awaitingLatcher(CountDownLatch latcher, TestListener jl) {
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
