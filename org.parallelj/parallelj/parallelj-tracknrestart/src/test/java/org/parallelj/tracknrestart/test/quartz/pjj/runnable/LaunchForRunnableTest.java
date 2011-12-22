package org.parallelj.tracknrestart.test.quartz.pjj.runnable;

import static org.junit.Assert.fail;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import junit.framework.Assert;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.parallelj.internal.reflect.ProgramAdapter.Adapter;
import org.parallelj.launching.quartz.ParalleljSchedulerFactory;
import org.parallelj.launching.quartz.ParalleljSchedulerRepository;
import org.parallelj.tracknrestart.databinding.ProgramFieldsBinder;
import org.parallelj.tracknrestart.plugins.TrackNRestartPluginAll;
import org.parallelj.tracknrestart.test.quartz.RootAbstractTest;
import org.parallelj.tracknrestart.test.quartz.TestHelper;
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
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.matchers.EverythingMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class LaunchForRunnableTest extends RootAbstractTest {

	@Test
	public void testfirst() {
		
		String restartId = null;
		String programQN = "org.parallelj.tracknrestart.test.quartz.pjj.flow.runnable.ForEachMyElement";
		String jobName = "AAA_TestJob3_runnable"+this.getClass().getSimpleName();
		String groupName = "DEFAULT";
		String triggerName = "AAA_TestTrigger3_runnable"+this.getClass().getSimpleName();
		List<People> l = new ArrayList<People>();
		l.add(new People("john", "lennon"));
		l.add(new People("freddie", "mercury"));
		l.add(new People("roger", "waters"));
		l.add(new People("chapi","chapo")); // "chapi" is expected failure
		l.add(new People("shane", "mcgowan"));
		int expectedFailures = 1;

		try {
			Class<? extends Adapter> jobClass = ((Class<? extends Adapter>) Class.forName(programQN));
			JobBuilder jobBuilder = newJob((Class<? extends Job>) jobClass);

			JobDataMap jobDataMap = new JobDataMap();
			jobDataMap.put("data1",l);
			
			JobDetail job = createJob(groupName, jobName, restartId, jobBuilder, jobDataMap);

			Trigger trigger = createTrigger(groupName, triggerName);
			
			CountDownLatch latcher = createLatcher(jl);
			
			sched.scheduleJob(job, trigger);
			// wait 5 seconds to give our jobs a chance to run
			try {
				Thread.sleep(5L * 1000L);
			} catch (Exception e) {
			}

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
				Assert.assertEquals(l.size(),resultSet.getInt("total"));
				int failures = resultSet.getInt("failure");
				Assert.assertEquals(expectedFailures, failures);
			} finally {
				TestHelper.getInstance().closeStatement(statement);
				TestHelper.getInstance().cleanupConnection(conn);
			}
			
		} catch (SchedulerException e) {
			e.printStackTrace();
			fail();
			return;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			fail();
			return;
		} catch (SQLException e) {
			e.printStackTrace();
			fail();
			return;
		}
	}

	@Test
	public void testRestart() {
		
		String restartId = "_LAST_";
		String programQN = "org.parallelj.tracknrestart.test.quartz.pjj.flow.runnable.ForEachMyElement";
		String jobName = "AAA_TestJob3_runnable"+this.getClass().getSimpleName();
		String groupName = "DEFAULT";
		String triggerName = "AAA_TestTrigger3_runnable"+this.getClass().getSimpleName();
		List<People> l = new ArrayList<People>();
		l.add(new People("john", "lennon"));
		l.add(new People("freddie", "mercury")); 
		l.add(new People("roger", "waters"));
		l.add(new People("chapa","chapo")); // "chapi" disappeared, replaced by "chapa"
		l.add(new People("shane", "mcgowan"));
		l.add(new People("leonard", "cohen")); // new "leonard" appearing
		int expectedFailures = 0;

		try {
			Class<? extends Adapter> jobClass = ((Class<? extends Adapter>) Class.forName(programQN));
			JobBuilder jobBuilder = newJob((Class<? extends Job>) jobClass);

			JobDataMap jobDataMap = new JobDataMap();
			jobDataMap.put("data1",l);
			
			JobDetail job = createJob(groupName, jobName, restartId, jobBuilder, jobDataMap);

			Trigger trigger = createTrigger(groupName, triggerName);
			
			CountDownLatch latcher = createLatcher(jl);
			
			sched.scheduleJob(job, trigger);
			// wait 5 seconds to give our jobs a chance to run
			try {
				Thread.sleep(5L * 1000L);
			} catch (Exception e) {
			}

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
				Assert.assertEquals(l.size(),resultSet.getInt("total"));
				int failures = resultSet.getInt("failure");
				Assert.assertEquals(expectedFailures, failures);
			} finally {
				TestHelper.getInstance().closeStatement(statement);
				TestHelper.getInstance().cleanupConnection(conn);
			}
			
		} catch (SchedulerException e) {
			e.printStackTrace();
			fail();
			return;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			fail();
			return;
		} catch (SQLException e) {
			e.printStackTrace();
			fail();
			return;
		}
	}

}


