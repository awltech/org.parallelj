package org.parallelj.tracknrestart.plugins;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


import org.parallelj.tracknrestart.TrackNRestartMessageKind;
import org.parallelj.tracknrestart.jdbc.JDBCSupport;
import org.parallelj.tracknrestart.listeners.ForEachListener;
import org.parallelj.tracknrestart.listeners.TrackNRestartListener;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.quartz.JobListener;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerListener;
import org.quartz.SchedulerMetaData;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.quartz.impl.jdbcjobstore.Constants;
import org.quartz.impl.matchers.EverythingMatcher;
import org.quartz.spi.SchedulerPlugin;

@Deprecated
public class TrackNRestartPlugin extends JDBCSupport implements SchedulerPlugin, JobListener, SchedulerListener {
	
	private static final long serialVersionUID = 1L;

	public static final String RESTARTED_FIRE_INSTANCE_ID = "restartedFireInstanceId";

	public static final String FOR_EACH_LISTENER = "forEachListener";

	private String name;

    private Scheduler scheduler;

	public String getName() {
		return name;
	}

    private String FETCH_JOB_DETAIL_TRACK = "SELECT DISTINCT " 
    	+ Constants.COL_SCHEDULER_NAME + ", "
    	+ Constants.COL_JOB_GROUP + ", "
    	+ Constants.COL_JOB_NAME
    	+ " FROM "
        + TABLE_PREFIX_SUBST + Constants.TABLE_JOB_DETAILS 
	    + " WHERE " 
    	+ Constants.COL_SCHEDULER_NAME + " = " + SCHED_NAME_SUBST + " AND "
    	+ Constants.COL_JOB_GROUP + " = ? AND "
    	+ Constants.COL_JOB_NAME + " = ? "; 
 
    private String FETCH_JOB_DETAIL_EXEC_TRACK = "SELECT " 
    	+ Constants.COL_SCHEDULER_NAME + ", "
    	+ Constants.COL_JOB_GROUP + ", "
    	+ Constants.COL_JOB_NAME + ", "
    	+ COL_UID_SUBST 
    	+ " FROM "
        + TABLE_PREFIX_SUBST + Constants.TABLE_JOB_DETAILS 
	    + " WHERE " 
    	+ Constants.COL_SCHEDULER_NAME + " = " + SCHED_NAME_SUBST + " AND "
    	+ Constants.COL_JOB_GROUP + " = ? AND "
    	+ Constants.COL_JOB_NAME + " = ? AND "
	    + COL_UID_SUBST + " = ?"; 
 
    private String FETCH_JOB_DETAIL_EXEC_LAST_TRACK = "SELECT MAX(" + COL_UID_SUBST + ") " 
    	+ Constants.COL_SCHEDULER_NAME + ", "
    	+ Constants.COL_JOB_GROUP + ", "
    	+ Constants.COL_JOB_NAME
    	+ " FROM "
        + TABLE_PREFIX_SUBST + Constants.TABLE_JOB_DETAILS 
	    + " WHERE " 
    	+ Constants.COL_SCHEDULER_NAME + " = " + SCHED_NAME_SUBST + " AND "
    	+ Constants.COL_JOB_GROUP + " = ? AND "
    	+ Constants.COL_JOB_NAME + " = ?"; 
 
	public void initialize(String pname, Scheduler scheduler)
			throws SchedulerException {
		this.name = pname;
		this.scheduler = scheduler;
		scheduler.getListenerManager().addJobListener(this,
				EverythingMatcher.allJobs());
		scheduler.getListenerManager().addSchedulerListener(this);
		TrackNRestartMessageKind.ITNRPLUGIN0001.format();
//		getLog().info("Registering Quartz Job Track&Restart Plug-in.");
	}

	private boolean fetchJob(Connection conn, JobKey jobKey)
			throws IOException, SQLException, SchedulerException {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement(rtp(FETCH_JOB_DETAIL_TRACK,
					scheduler.getSchedulerName()));
			ps.setString(1, jobKey.getGroup());
			ps.setString(2, jobKey.getName());
			rs = ps.executeQuery();
			return rs.next();
		} finally {
			closeStatement(ps);
			cleanupConnection(conn);
		}
	}

	private boolean fetchJobExec(Connection conn, JobKey jobKey, String restart)
			throws IOException, SQLException, SchedulerException {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement(rtp(FETCH_JOB_DETAIL_EXEC_TRACK,
					scheduler.getSchedulerName()));
			ps.setString(1, jobKey.getGroup());
			ps.setString(2, jobKey.getName());
			ps.setString(3, restart);
			rs = ps.executeQuery();
			return rs.next();
		} finally {
			closeStatement(ps);
			cleanupConnection(conn);
		}
	}

	private String fetchJobExecLast(Connection conn, JobKey jobKey)
			throws IOException, SQLException, SchedulerException {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement(rtp(FETCH_JOB_DETAIL_EXEC_LAST_TRACK,
					scheduler.getSchedulerName()));
			ps.setString(1, jobKey.getGroup());
			ps.setString(2, jobKey.getName());
			rs = ps.executeQuery();
			if (rs.next()){
				return rs.getString(1);
			}
			return null;
		} finally {
			closeStatement(ps);
			cleanupConnection(conn);
		}
	}

	public void jobToBeExecuted(JobExecutionContext context) {
		try {
			ForEachListener forEachListener = 
					new TrackNRestartListener(
							dataSource, 
							tablePrefix, 
							this.getName(),
							scheduler.getSchedulerName(),
							context.getJobDetail().getKey().getGroup(),
							context.getJobDetail().getKey().getName(),
							context.getFireInstanceId());
			context.getJobDetail().getJobDataMap().put(FOR_EACH_LISTENER, forEachListener);
		} catch (Exception e) {
			TrackNRestartMessageKind.ETNRPLUGIN0002.format(e);
//			getLog().error("Unexpected exception.",e);
		}
	}

	public void jobExecutionVetoed(JobExecutionContext context) {
		// TODO Auto-generated method stub
		
	}

	public void jobWasExecuted(JobExecutionContext context,
			JobExecutionException jobException) {
		// TODO Auto-generated method stub
		
	}

	public void start() {
		try {
			getLog().debug("----------------- Start scheduler "+scheduler.getSchedulerName());
		} catch (SchedulerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void shutdown() {
		try {
			getLog().debug("----------------- Shutdown scheduler "+scheduler.getSchedulerName());
		} catch (SchedulerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void jobScheduled(Trigger trigger) {
		getLog().debug("Trigger "+trigger.getKey()+" scheduled Job "+trigger.getJobKey()+".");
	}

	public void jobUnscheduled(TriggerKey triggerKey) {
		getLog().debug("Trigger "+triggerKey+" unscheduled Job.");
	}

	public void triggerFinalized(Trigger trigger) {
		getLog().debug("Trigger "+trigger.getKey()+" finalized.");
	}

	public void triggerPaused(TriggerKey triggerKey) {
		getLog().debug("Trigger "+triggerKey+" paused.");
	}

	public void triggersPaused(String triggerGroup) {
		// TODO Auto-generated method stub
	}

	public void triggerResumed(TriggerKey triggerKey) {
		getLog().debug("Trigger "+triggerKey+" resumed.");
	}

	public void triggersResumed(String triggerGroup) {
		// TODO Auto-generated method stub
	}

	public void jobAdded(JobDetail jobDetail) {
		TrackNRestartMessageKind.ITNRPLUGIN0003.format(jobDetail.getKey());
//		getLog().info("Job "+jobDetail.getKey()+" added.");
		JobKey jobKey = null;
		try {
			jobKey = jobDetail.getKey();
			JobDataMap jobDataMap = jobDetail.getJobDataMap();
			String restart = jobDataMap.getString(RESTARTED_FIRE_INSTANCE_ID);
			if (fetchJob(getNonManagedTXConnection(), jobKey)){ // group.job exists
				if (restart == null){
					TrackNRestartMessageKind.ITNRPLUGIN0004.format(jobKey);
//					getLog().info(jobKey+" is running in simple tracking (non-restarting) mode.");
					TrackNRestartMessageKind.WTNRPLUGIN0005.format(jobKey);
//					getLog().warn("At least one "+jobKey+" execution already exists in tracking history.");
				} else {
					if (restart.equals("_LAST_")){
						restart = fetchJobExecLast(getNonManagedTXConnection(), jobKey);
						if (restart != null){
							jobDataMap.put(RESTARTED_FIRE_INSTANCE_ID, restart);
							TrackNRestartMessageKind.ITNRPLUGIN0006.format(jobKey, restart);
//							getLog().info("Replacing "+jobKey+" after resolving restarted id from _LAST_ to "+restart+".");
							scheduler.addJob(jobDetail, true); // WARNING recursivity !
						} else {
							TrackNRestartMessageKind.ETNRPLUGIN0007.format(jobKey, restart);
//							getLog().error("Unable to restart "+jobKey+" caused by previous execution id #"+restart+" not found in tracking history.");
							TrackNRestartMessageKind.WTNRPLUGIN0008.format(jobKey);
//							getLog().warn("Deleting "+jobKey+" to prevent unexpected execution.");
							scheduler.deleteJob(jobKey);
						}
					} else {
						if (fetchJobExec(getNonManagedTXConnection(), jobKey, restart)){
							TrackNRestartMessageKind.ITNRPLUGIN0010.format(jobKey);
//							getLog().info("Restarting "+jobKey+" #"+restart+".");
						} else {
							TrackNRestartMessageKind.ETNRPLUGIN0007.format(jobKey, restart);
//							getLog().error("Unable to restart "+jobKey+" caused by previous execution id #"+restart+" not found in tracking history.");
							TrackNRestartMessageKind.WTNRPLUGIN0008.format(jobKey);
//							getLog().warn("Deleting "+jobKey+" to prevent unexpected execution.");
							scheduler.deleteJob(jobKey);
						}
					}
				}
			} else { // group.job doesn't exist
				if (restart == null){
					TrackNRestartMessageKind.ITNRPLUGIN0004.format(jobKey);
//					getLog().info(jobKey+" is running in simple tracking (non-restarting) mode.");
//					TrackNRestartMessageKind.ITNRPLUGIN0015.format(jobKey);
//					getLog().info("First tracked execution of "+jobKey+".");
				} else {
					TrackNRestartMessageKind.ETNRPLUGIN0013.format(jobKey);
//					getLog().error("Unable to restart "+jobKey+" caused by no previous execution in tracking history.");
					TrackNRestartMessageKind.ETNRPLUGIN0014.format(jobKey);
//					getLog().warn("Deleting "+jobKey+" to prevent unexpected execution.");
					scheduler.deleteJob(jobKey);
				}
			}
		} catch (Exception e) {
			try {
				scheduler.deleteJob(jobKey);
			} catch (SchedulerException e1) {
				//TODO Verify such case doesn't freeze
				getLog().error("Deleting "+jobKey+" caused exception.", e);
			}
		}
	}

	public void jobDeleted(JobKey jobKey) {
		getLog().debug("Job "+jobKey+" deleted.");
	}

	public void jobPaused(JobKey jobKey) {
		getLog().debug("Job "+jobKey+" paused.");
	}

	public void jobsPaused(String jobGroup) {
		// TODO Auto-generated method stub
	}

	public void jobResumed(JobKey jobKey) {
		getLog().debug("Job "+jobKey+" resumed.");
	}

	public void jobsResumed(String jobGroup) {
		// TODO Auto-generated method stub
	}

	public void schedulerError(String msg, SchedulerException cause) {
		try {
			getLog().error("----------------- Scheduler "+scheduler.getSchedulerName()+ " in error", cause);
		} catch (SchedulerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void schedulerInStandbyMode() {
		try {
			getLog().debug("----------------- Scheduler "+scheduler.getSchedulerName()+ " in standby mode");
		} catch (SchedulerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void schedulerStarted() {
		try {
			getLog().debug("----------------- Scheduler "+scheduler.getSchedulerName()+ " started");
		} catch (SchedulerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void schedulerShutdown() {
		try {
			getLog().debug("----------------- Scheduler "+scheduler.getSchedulerName()+ " shutdown");
			SchedulerMetaData metaData = scheduler.getMetaData();
			getLog().info("Executed " + metaData.getNumberOfJobsExecuted() + " jobs.");
		} catch (SchedulerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void schedulerShuttingdown() {
		try {
			getLog().debug("----------------- Scheduler "+scheduler.getSchedulerName()+ " shutting down");
		} catch (SchedulerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void schedulingDataCleared() {
		try {
			getLog().info("----------------- Scheduler "+scheduler.getSchedulerName()+ " cleared");
		} catch (SchedulerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}