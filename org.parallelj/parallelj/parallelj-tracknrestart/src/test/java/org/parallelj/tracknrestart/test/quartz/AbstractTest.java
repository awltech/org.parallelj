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
public abstract class AbstractTest extends RootAbstractTest {

	protected abstract String getProgramQN();
	
	protected int getNumberJob1() {
		return 5;
	}
	protected int getNumberJob22() {
		return 30;
	}
	
	/**
	 * First launch
	 */
	@Test
	public void test1_initial() {
		
		String restartId = null;
		String programQN = getProgramQN();
		String jobName = "JJP_TestJob1_"+this.getClass().getSimpleName();
		String groupName = "DEFAULT";
		String triggerName = "JJP_TestTrigger1_"+this.getClass().getSimpleName();
		List<String> params = Arrays.asList(new String[]{"a","b","c"});

		try {
			Class<? extends Adapter> jobClass = ((Class<? extends Adapter>) Class.forName(programQN));
			JobBuilder jobBuilder = newJob((Class<? extends Job>) jobClass);

			JobDataMap jobDataMap = new JobDataMap();
			jobDataMap.put("data1",params);

			JobDetail job = createJob(groupName, jobName, restartId, jobBuilder, jobDataMap);
			
			Trigger trigger = createTrigger(groupName, triggerName);
			
			CountDownLatch latcher = createLatcher(jl);
			
			sched.scheduleJob(job, trigger);

			awaitingLatcher(latcher, jl);
			
			Statement statement = null;
			ResultSet resultSet = null;
			Connection conn = null;
			try {
				conn = TestHelper.getInstance().getNonManagedTXConnection();
				statement = conn.createStatement();
				resultSet = statement.executeQuery("select * from (" + TestHelper.req + ") as report where report.job_name='" +job.getKey().getName()+ "' and report.job_group='"+job.getKey().getGroup()+"'");
				Assert.assertTrue(resultSet.first());
				Assert.assertNull(resultSet.getString("restarted_uid"));
				Assert.assertEquals(params.size(),resultSet.getInt("total"));
			} finally {
				TestHelper.getInstance().closeStatement(statement);
				TestHelper.getInstance().cleanupConnection(conn);
			}
			
			
		} catch (SchedulerException e) {
			fail();
			return;
		} catch (ClassNotFoundException e) {
			fail();
			return;
		} catch (SQLException e) {
			e.printStackTrace();
			fail();
			return;
		}
	}

	/**
	 * Restart launch from test1 but wrong restarted id
	 */
	@Test
	public void test1_rest_but_no_rid() {
		String restartId = "_WRONG_";
		String programQN = getProgramQN();
		String jobName = "JJP_TestJob1_"+this.getClass().getSimpleName();
		String groupName = "DEFAULT";
		String triggerName = "JJP_TestTrigger1_"+this.getClass().getSimpleName();
		List<String> params = Arrays.asList(new String[]{"a","b","c"});

		try {
			Class<? extends Adapter> jobClass = ((Class<? extends Adapter>) Class.forName(programQN));
			JobBuilder jobBuilder = newJob((Class<? extends Job>) jobClass);

			JobDataMap jobDataMap = new JobDataMap();
			jobDataMap.put("data1",params);

			JobDetail job = createJob(groupName, jobName, restartId, jobBuilder, jobDataMap);

			Trigger trigger = createTrigger(groupName, triggerName);
			
			CountDownLatch latcher = createLatcher(jl);
			
			sched.scheduleJob(job, trigger);

			awaitingLatcher(latcher, jl);

			Statement statement = null;
			ResultSet resultSet = null;
			Connection conn = null;
			try {
				conn = TestHelper.getInstance().getNonManagedTXConnection();
				statement = conn.createStatement();
				resultSet = statement.executeQuery("select * from (" + TestHelper.req + ") as report where report.job_name='" +job.getKey().getName()+ "' and report.job_group='"+job.getKey().getGroup()+"'");
				Assert.assertTrue(resultSet.first());
				Assert.assertNull(resultSet.getString("restarted_uid"));
//				String uid = resultSet.getString("uid");
				Assert.assertFalse(resultSet.next());
//				Assert.assertEquals(uid, resultSet.getString("restarted_uid"));
//				Assert.assertEquals(params.size(),resultSet.getInt("total"));
			} finally {
				TestHelper.getInstance().closeStatement(statement);
				TestHelper.getInstance().cleanupConnection(conn);
			}
			
		} catch (SchedulerException e) {
			e.printStackTrace();
			fail();
			return;
		} catch (ClassNotFoundException e) {
			fail();
			return;
		} catch (SQLException e) {
			fail();
			return;
		}
	}

	/**
	 * Restart launch from test1 (with _LAST_)
	 */
	@Test
	public void test1_rest_with_last_rid() {
		String restartId = "_LAST_";
		String programQN = getProgramQN();
		String jobName = "JJP_TestJob1_"+this.getClass().getSimpleName();
		String groupName = "DEFAULT";
		String triggerName = "JJP_TestTrigger1_"+this.getClass().getSimpleName();
		List<String> params = Arrays.asList(new String[]{"a","b","c"});

		try {
			Class<? extends Adapter> jobClass = ((Class<? extends Adapter>) Class.forName(programQN));
			JobBuilder jobBuilder = newJob((Class<? extends Job>) jobClass);

			JobDataMap jobDataMap = new JobDataMap();
			jobDataMap.put("data1",params);

			JobDetail job = createJob(groupName, jobName, restartId, jobBuilder, jobDataMap);

			Trigger trigger = createTrigger(groupName, triggerName);
			
			CountDownLatch latcher = createLatcher(jl);
			
			sched.scheduleJob(job, trigger);

			awaitingLatcher(latcher, jl);

			Statement statement = null;
			ResultSet resultSet = null;
			Connection conn = null;
			try {
				conn = TestHelper.getInstance().getNonManagedTXConnection();
				statement = conn.createStatement();
				resultSet = statement.executeQuery("select * from (" + TestHelper.req + ") as report where report.job_name='" +job.getKey().getName()+ "' and report.job_group='"+job.getKey().getGroup()+"'");
				Assert.assertTrue(resultSet.first());
				Assert.assertNull(resultSet.getString("restarted_uid"));
				String uid = resultSet.getString("uid");
				Assert.assertTrue(resultSet.next());
				Assert.assertEquals(uid, resultSet.getString("restarted_uid"));
				Assert.assertEquals(params.size(),resultSet.getInt("total"));
			} finally {
				TestHelper.getInstance().closeStatement(statement);
				TestHelper.getInstance().cleanupConnection(conn);
			}
			
		} catch (SchedulerException e) {
			e.printStackTrace();
			fail();
			return;
		} catch (ClassNotFoundException e) {
			fail();
			return;
		} catch (SQLException e) {
			fail();
			return;
		}
	}

	/**
	 * Restart launch from unknown job
	 */
	@Test
	public void test2_rest_but_no_initial_job() {
		String restartId = "_LAST_";
		String programQN = getProgramQN();
		String jobName = "JJP_UNKNOWN_JOB_"+this.getClass().getSimpleName();
		String groupName = "DEFAULT";
		String triggerName = "JJP__UNKNOWN_TRIGGER_"+this.getClass().getSimpleName();
		List<String> params = Arrays.asList(new String[]{"a","b","c"});

		try {
			Class<? extends Adapter> jobClass = ((Class<? extends Adapter>) Class.forName(programQN));
			JobBuilder jobBuilder = newJob((Class<? extends Job>) jobClass);

			JobDataMap jobDataMap = new JobDataMap();
			jobDataMap.put("data1",params);

			JobDetail job = createJob(groupName, jobName, restartId, jobBuilder, jobDataMap);

			Trigger trigger = createTrigger(groupName, triggerName);
			
			CountDownLatch latcher = createLatcher(jl);
			
			sched.scheduleJob(job, trigger);

			awaitingLatcher(latcher, jl);

			Statement statement = null;
			ResultSet resultSet = null;
			Connection conn = null;
			try {
				conn = TestHelper.getInstance().getNonManagedTXConnection();
				statement = conn.createStatement();
				resultSet = statement.executeQuery("select * from (" + TestHelper.req + ") as report where report.job_name='" +job.getKey().getName()+ "' and report.job_group='"+job.getKey().getGroup()+"'");
				Assert.assertFalse(resultSet.first());
			} finally {
				TestHelper.getInstance().closeStatement(statement);
				TestHelper.getInstance().cleanupConnection(conn);
			}
			
		} catch (SchedulerException e) {
			e.printStackTrace();
			fail();
			return;
		} catch (ClassNotFoundException e) {
			fail();
			return;
		} catch (SQLException e) {
			fail();
			return;
		}
	}

	/**
	 * second "a" leads to duplicate key then flow is aborted 
	 */
	@Test
	public void test3() {
		
		String restartId = null;
		String programQN = getProgramQN();
		String jobName = "JJP_TestJob3_"+this.getClass().getSimpleName();
		String groupName = "DEFAULT";
		String triggerName = "JJP_TestTrigger3_"+this.getClass().getSimpleName();
		List<String> params = Arrays.asList(new String[]{"a","b","a"});
		int expectedProcessed = 2;

		try {
			Class<? extends Adapter> jobClass = ((Class<? extends Adapter>) Class.forName(programQN));
			JobBuilder jobBuilder = newJob((Class<? extends Job>) jobClass);

			JobDataMap jobDataMap = new JobDataMap();
			jobDataMap.put("data1",params);
			
			JobDetail job = createJob(groupName, jobName, restartId, jobBuilder, jobDataMap);

			Trigger trigger = createTrigger(groupName, triggerName);
			
			CountDownLatch latcher = createLatcher(jl);
			
			sched.scheduleJob(job, trigger);

			awaitingLatcher(latcher, jl);

			Statement statement = null;
			ResultSet resultSet = null;
			Connection conn = null;
			try {
				conn = TestHelper.getInstance().getNonManagedTXConnection();
				statement = conn.createStatement();
				resultSet = statement.executeQuery("select * from (" + TestHelper.req + ") as report where report.job_name='" +job.getKey().getName()+ "' and report.job_group='"+job.getKey().getGroup()+"'");
				Assert.assertTrue(resultSet.first());
				Assert.assertNull(resultSet.getString("restarted_uid"));
//				Assert.assertEquals("ABORTED", resultSet.getString("result"));
				Assert.assertEquals("ABORTED", TestHelper.getInstance().getJobDataMapResultFromBlob(resultSet).get(QuartzContextAdapter.RETURN_CODE));
				Assert.assertEquals(expectedProcessed, TestHelper.getInstance().getJobDataMapResultFromBlob(resultSet).get("processed"));
				Assert.assertFalse(params.size() == resultSet.getInt("total"));
			} catch (IOException e) {
				fail();
				return;
			} finally {
				TestHelper.getInstance().closeStatement(statement);
				TestHelper.getInstance().cleanupConnection(conn);
			}
			

		} catch (SchedulerException e) {
			e.printStackTrace();
			fail();
			return;
		} catch (ClassNotFoundException e) {
			fail();
			return;
		} catch (SQLException e) {
			e.printStackTrace();
			fail();
			return;
		}
	}

	/**
	 * mass test : n jobs sharing the same root program
	 */
	@Test
	public void test4() {
		
		String restartId = null;
		String programQN = getProgramQN();
		String groupName = "DEFAULT";
		int n = getNumberJob1(); 
		String[] letters = {"a","b","c","d","e","f","g","h","i","j","k","l","m","n","o","p","q","r","s","t","u","v","w","x","y","z"};

		try {
			
			JobDetail[] job = new JobDetail[n];
			Trigger[] trigger = new Trigger[n];
			
			for (int i = 0; i < n; i++) {
				String triggerName = "JJP_TestTrigger_Concurrent_"+this.getClass().getSimpleName()+i;
				String jobName = "JJP_TestJob_Concurrent_"+this.getClass().getSimpleName()+i;
				String[] letters2 = new String[letters.length];
				for (int j = 0; j < letters.length; j++) {
					letters2[j]=letters[j]+i;
				}
				List<String> params = Arrays.asList(letters2);
				Class<? extends Adapter> jobClass = ((Class<? extends Adapter>) Class.forName(programQN));
				JobBuilder jobBuilder = newJob((Class<? extends Job>) jobClass);
				JobDataMap jobDataMap = new JobDataMap();
				jobDataMap.put("data1",params);
				
				job[i] = createJob(groupName, jobName, restartId, jobBuilder, jobDataMap);
				trigger[i] = createTrigger(groupName, triggerName);
			}
			
			CountDownLatch latcher = createLatcher(jl,n);
			
			for (int i = 0; i < n; i++) {
				sched.scheduleJob(job[i], trigger[i]);
			}
			
			awaitingLatcher(latcher, jl);
			
			Statement statement = null;
			ResultSet resultSet = null;
			Connection conn = null;

			for (int i = 0; i < n; i++) {
				try {
					conn = TestHelper.getInstance().getNonManagedTXConnection();
					statement = conn.createStatement();
					resultSet = statement.executeQuery("select * from (" + TestHelper.req + ") as report where report.job_name='" +job[i].getKey().getName()+ "' and report.job_group='"+job[i].getKey().getGroup()+"'");
					Assert.assertTrue(resultSet.first());
					Assert.assertEquals(letters.length,resultSet.getInt("total"));
				} finally {
					TestHelper.getInstance().closeStatement(statement);
					TestHelper.getInstance().cleanupConnection(conn);
				}
			}
			
		} catch (SchedulerException e) {
			fail();
			return;
		} catch (ClassNotFoundException e) {
			fail();
			return;
		} catch (SQLException e) {
			e.printStackTrace();
			fail();
			return;
		}
	}

	/**
	 * run job after (same) job until full SUCCESS ... 
	 */
	@Test
	public void test5() {
		
		String restartId = null;
		String programQN = getProgramQN();
		String groupName = "DEFAULT";
		String triggerName = "JJP_TestTrigger_Serialized_"+this.getClass().getSimpleName();
		String jobName = "JJP_TestJob_Serialized_"+this.getClass().getSimpleName();
		String[] letters = {"a","b","c","d","e","f","g","h","i","j","k","l","m","n","o","p","q","r","s","t","u","v","w","x","y","z"};
		List<String> params = Arrays.asList(letters);
		int n = getNumberJob22();
		
		String result = ReturnCodes.FAILURE.name();
		
		try {
			JobDetail job = null;

			for (int i = 0; i < n && ReturnCodes.FAILURE.name().equals(result); i++) {

				Class<? extends Adapter> jobClass = ((Class<? extends Adapter>) Class.forName(programQN));
				JobBuilder jobBuilder = newJob((Class<? extends Job>) jobClass);
				JobDataMap jobDataMap = new JobDataMap();
				jobDataMap.put("data1",params);
				
				if (i>0) {
					restartId = "_LAST_";
				}

				job = createJob(groupName, jobName, restartId, jobBuilder, jobDataMap);
				Trigger trigger = createTrigger(groupName, triggerName);
				
				CountDownLatch latcher = createLatcher(jl);
				
				sched.scheduleJob(job, trigger);

				awaitingLatcher(latcher, jl);
				
//				result = (String)jl.getResult();
				Map<String, Serializable> map = (Map<String, Serializable>)jl.getResult();
				result = (String)map.get(QuartzContextAdapter.RETURN_CODE);

			}

			Statement statement = null;
			ResultSet resultSet = null;
			Connection conn = null;
			try {
				conn = TestHelper.getInstance().getNonManagedTXConnection();
				statement = conn.createStatement();
				resultSet = statement.executeQuery("select * from (" + TestHelper.req + ") as report where report.job_name='" + job.getKey().getName()	+ "' and report.job_group='" + job.getKey().getGroup() + "'");

				Assert.assertTrue(resultSet.first());
				Assert.assertNull(resultSet.getString("restarted_uid"));
				Assert.assertEquals(params.size(),resultSet.getInt("total"));
				while (!resultSet.isLast()) {
					String uid = resultSet.getString("uid");
//					Assert.assertEquals("FAILURE",resultSet.getString("result"));
					Assert.assertEquals("FAILURE", TestHelper.getInstance().getJobDataMapResultFromBlob(resultSet).get(QuartzContextAdapter.RETURN_CODE));
					int failures = resultSet.getInt("failure");
					Assert.assertTrue(0 < failures);
					Assert.assertTrue(resultSet.next());
					Assert.assertEquals(uid,resultSet.getString("restarted_uid"));
					Assert.assertEquals(params.size(),resultSet.getInt("total"));
					Assert.assertTrue(failures >= resultSet.getInt("failure"));
				}
//				Assert.assertEquals("SUCCESS",resultSet.getString("result"));
				Assert.assertEquals("SUCCESS", TestHelper.getInstance().getJobDataMapResultFromBlob(resultSet).get(QuartzContextAdapter.RETURN_CODE));
				Assert.assertEquals(0, resultSet.getInt("failure"));

			} catch (IOException e) {
				fail();
				return;
			} finally {
				TestHelper.getInstance().closeStatement(statement);
				TestHelper.getInstance().cleanupConnection(conn);
			}

		} catch (SchedulerException e) {
			fail();
			return;
		} catch (ClassNotFoundException e) {
			fail();
			return;
		} catch (SQLException e) {
			e.printStackTrace();
			fail();
			return;
		}
	}

}
