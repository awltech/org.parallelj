package org.parallelj.tracknrestart.plugins;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;


import org.parallelj.tracknrestart.TrackNRestartMessageKind;
//import org.parallelj.tracknrestart.jdbc.JDBCSupport;
import org.parallelj.tracknrestart.aspects.QuartzContextAdapter;
import org.parallelj.tracknrestart.aspects.TrackNRestartException;
import org.parallelj.tracknrestart.jdbc.JDBCSupport;
import org.parallelj.tracknrestart.listeners.ForEachListener;
import org.parallelj.tracknrestart.listeners.TrackNRestartListener;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.quartz.JobListener;
import org.quartz.JobPersistenceException;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerListener;
import org.quartz.SchedulerMetaData;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.quartz.impl.jdbcjobstore.Constants;
import org.quartz.impl.matchers.EverythingMatcher;
import org.quartz.spi.SchedulerPlugin;

public class TrackNRestartPluginAll extends JDBCSupport implements SchedulerPlugin, JobListener, SchedulerListener {
	
	private static final String JOB_IDENTIFICATION_COMPLETE = "_JOB_IDENTIFICATION_COMPLETE_";

	private static final long serialVersionUID = 1L;

	public static final String RESTARTED_FIRE_INSTANCE_ID = "_RESTARTED_FIRE_INSTANCE_ID_";

	public static final String FOR_EACH_LISTENER = "_FOR_EACH_LISTENER_";

	private String name;

    private Scheduler scheduler;

    private String jobToBeFiredMessage = "Job {1}.{0} [id #{8}] fired (by trigger {4}.{3}) at: {2, date, HH:mm:ss MM/dd/yyyy} with params [{9}]";

	private String jobSuccessMessage = "Job {1}.{0} [id #{8}] execution complete at {2, date, HH:mm:ss MM/dd/yyyy} and reports: {9}";

	private String jobFailedMessage = "Job {1}.{0} [id #{8}] execution failed at {2, date, HH:mm:ss MM/dd/yyyy} and reports: {9} caused by {10}";

	private String jobWasVetoedMessage = "Job {1}.{0} was vetoed.  It was to be fired (by trigger {4}.{3}) at: {2, date, HH:mm:ss MM/dd/yyyy}";

	public String getName() {
		return name;
	}

//	private static String COL_RESULT_SUBST = "RESULT";
//
//	private static String COL_RESTARTED_UID_SUBST = "RESTARTED_UID";
//
	/**
	 * Get the message that is logged when a Job successfully completes its
	 * execution.
	 */
	public String getJobSuccessMessage() {
		return jobSuccessMessage;
	}

	/**
	 * Get the message that is logged when a Job fails its execution.
	 */
	public String getJobFailedMessage() {
		return jobFailedMessage;
	}

	/**
	 * Get the message that is logged when a Job is about to execute.
	 */
	public String getJobToBeFiredMessage() {
		return jobToBeFiredMessage;
	}

	/**
	 * Get the message that is logged when a Job execution is vetoed by a
	 * trigger listener.
	 */
	public String getJobWasVetoedMessage() {
		return jobWasVetoedMessage;
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
    	+ Constants.COL_JOB_DATAMAP + ", "
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
 
    private String INSERT_JOB_DETAIL_TRACK = "INSERT INTO "
            + TABLE_PREFIX_SUBST + Constants.TABLE_JOB_DETAILS 
            + " (" 
            + Constants.COL_SCHEDULER_NAME + ", " 
            + Constants.COL_JOB_NAME + ", " 
            + Constants.COL_JOB_GROUP + ", "
            + COL_UID_SUBST + ", " 
            + Constants.COL_DESCRIPTION + ", "
            + Constants.COL_JOB_CLASS + ", " 
            + Constants.COL_IS_DURABLE + ", " 
            + Constants.COL_IS_NONCONCURRENT +  ", "
            + Constants.COL_IS_UPDATE_DATA + ", " 
            + Constants.COL_REQUESTS_RECOVERY + ", "
            + Constants.COL_JOB_DATAMAP + ", " 
            + COL_RESULT_SUBST + ", "
            + COL_RESTARTED_UID_SUBST + ", "
            + COL_RETURN_CODE_SUBST
            + ") " 
            + " VALUES(" + SCHED_NAME_SUBST + ", ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

	public void initialize(String pname, Scheduler scheduler)
			throws SchedulerException {
		this.name = pname;
		this.scheduler = scheduler;
		scheduler.getListenerManager().addJobListener(this,
				EverythingMatcher.allJobs());
		scheduler.getListenerManager().addSchedulerListener(this);
		TrackNRestartMessageKind.ITNRPLUGIN0001.format();
		//getLog().info("Registering Quartz Job Track&Restart Plug-in.");
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

	private Object getJobExecParams(Connection conn, JobKey jobKey, String restart)
			throws IOException, SQLException, SchedulerException, ClassNotFoundException {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement(rtp(FETCH_JOB_DETAIL_EXEC_TRACK,
					scheduler.getSchedulerName()));
			ps.setString(1, jobKey.getGroup());
			ps.setString(2, jobKey.getName());
			ps.setString(3, restart);
			rs = ps.executeQuery();
			rs.next();
			return getJobDataFromBlob(rs, "JOB_DATA");
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
		//getLog().info("Job "+jobDetail.getKey()+" added.");
		JobKey jobKey = jobDetail.getKey();
		JobDataMap jobDataMap = jobDetail.getJobDataMap();
		String restart = jobDataMap.getString(RESTARTED_FIRE_INSTANCE_ID);
		try {
			if (fetchJob(getNonManagedTXConnection(), jobKey)){ // group.job exists
				if (restart == null){
					TrackNRestartMessageKind.ITNRPLUGIN0004.format(jobKey);
					//getLog().info(jobKey+" is running in simple tracking (non-restarting) mode.");
					TrackNRestartMessageKind.WTNRPLUGIN0005.format(jobKey);
					//getLog().warn("At least one "+jobKey+" execution already exists in tracking history.");
				} else {
					if (restart.trim().equals("_LAST_")){ // Retrieve last execution to restart it
						restart = fetchJobExecLast(getNonManagedTXConnection(), jobKey);
						if (restart != null){

							jobDetail.getJobBuilder().usingJobData(RESTARTED_FIRE_INSTANCE_ID, restart);
							jobDetail.getJobBuilder().usingJobData(JOB_IDENTIFICATION_COMPLETE,false); 
							
							TrackNRestartMessageKind.ITNRPLUGIN0006.format(jobKey, restart);
							//getLog().info("Replacing "+jobKey+" after resolving restarted id from _LAST_ to "+restart+".");

							scheduler.addJob(jobDetail, true); // WARNING recursivity !

						} else {
							//TrackNRestartMessageKind.ETNRPLUGIN0007.format(jobKey, restart);
							//getLog().error("Unable to restart "+jobKey+" caused by previous execution id #"+restart+" not found in tracking history.");
							TrackNRestartMessageKind.WTNRPLUGIN0008.format(jobKey);
							//getLog().warn("Deleting "+jobKey+" to prevent unexpected execution.");
							scheduler.deleteJob(jobKey);
							throw new TrackNRestartException(TrackNRestartMessageKind.ETNRPLUGIN0007.format(jobKey, restart));
						}
					} else {  // Retrieve numbered execution to restart it
						if (!jobDataMap.getBoolean(JOB_IDENTIFICATION_COMPLETE)) { // to break recursivity
							if (fetchJobExec(getNonManagedTXConnection(),jobKey, restart)) { // found

								JobDataMap currentJobDataMap = jobDataMap; // save current JDM
								JobDataMap previousJobDataMap = (JobDataMap) getJobExecParams(getNonManagedTXConnection(), jobKey, restart); // retrieve JDM from job to restart
								jobDetail.getJobBuilder().usingJobData(previousJobDataMap); // override current with previous JDM
								jobDetail.getJobBuilder().usingJobData(currentJobDataMap); // override overridden with current JDM (especially restart id)
								jobDetail.getJobBuilder().usingJobData(JOB_IDENTIFICATION_COMPLETE,true); 
								
								TrackNRestartMessageKind.ITNRPLUGIN0010.format(jobKey, restart);
								//getLog().info("Restarting "+jobKey+" #"+restart+".");

								scheduler.addJob(jobDetail, true); // WARNING recursivity !

							} else { // not found
								//TrackNRestartMessageKind.ETNRPLUGIN0007.format(jobKey, restart); // not found
								//getLog().error("Unable to restart "+jobKey+" caused by previous execution id #"+restart+" not found in tracking history.");
								TrackNRestartMessageKind.WTNRPLUGIN0008.format(jobKey);
								//getLog().warn("Deleting "+jobKey+" to prevent unexpected execution.");
								scheduler.deleteJob(jobKey);
								throw new TrackNRestartException(TrackNRestartMessageKind.ETNRPLUGIN0007.format(jobKey, restart));
							}
						}
					}
				}
			} else { // group.job doesn't exist
				if (restart == null){
					TrackNRestartMessageKind.ITNRPLUGIN0012.format(jobKey);
					//getLog().info(jobKey+" is running in simple tracking (non-restarting) mode.");
				} else {
					//TrackNRestartMessageKind.ETNRPLUGIN0013.format(jobKey);
					//getLog().error("Unable to restart "+jobKey+" caused by no previous execution in tracking history.");
					TrackNRestartMessageKind.WTNRPLUGIN0008.format(jobKey);
					//getLog().warn("Deleting "+jobKey+" to prevent unexpected execution.");
					scheduler.deleteJob(jobKey);
					throw new TrackNRestartException(TrackNRestartMessageKind.ETNRPLUGIN0013.format(jobKey));
				}
			}
		} catch (TrackNRestartException e) {
			throw e;
		} catch (Exception e) {
			try {
				e.printStackTrace();
				scheduler.deleteJob(jobKey);
			} catch (SchedulerException e1) {
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

	
	//--------------------------------------------------------------------------------
	
	/**
	 * @see org.quartz.JobListener#jobToBeExecuted(JobExecutionContext)
	 */
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
			//getLog().error("Unexpected exception.",e);
		}

		Trigger trigger = context.getTrigger();

		JobDetail jobDetail = context.getJobDetail();
		JobDataMap jobDataMap = jobDetail.getJobDataMap();

		String params = "";
		for (Iterator<Entry<String, Object>> iterator = jobDataMap.entrySet().iterator(); iterator
				.hasNext();) {
			Entry<String,Object> entry = (Entry<String,Object>) iterator.next();
			params+=entry.getKey()+"="+entry.getValue();
			if (iterator.hasNext()){
				params+=", ";
			}
		}

		Object[] args = { 
				jobDetail.getKey().getName(),
				jobDetail.getKey().getGroup(),
				new java.util.Date(),
				trigger.getKey().getName(),
				trigger.getKey().getGroup(),
				trigger.getPreviousFireTime(),
				trigger.getNextFireTime(),
				Integer.valueOf(context.getRefireCount()),
				context.getFireInstanceId(),
				params
				};

		try {

			//TODO : not for JobToBeFired ?
//			insertJobDetail(this.getNonManagedTXConnection(), context);
			getLog().info(MessageFormat.format(getJobToBeFiredMessage(), args));
		} catch (Exception e) {
			getLog().error(MessageFormat.format(getJobToBeFiredMessage(), args, e));
		}
	}

	/**
	 * @see org.quartz.JobListener#jobWasExecuted(JobExecutionContext,
	 *      JobExecutionException)
	 */
	public void jobWasExecuted(JobExecutionContext context,
			JobExecutionException jobException) {

		Trigger trigger = context.getTrigger();

		Object[] args = null;


		String result = "";
		Object oResult = context.getResult();
		if (oResult instanceof JobDataMap) {
			for (Iterator<Entry<String, Object>> iterator = ((JobDataMap)oResult).entrySet().iterator(); iterator
					.hasNext();) {
				Entry<String,Object> entry = (Entry<String,Object>) iterator.next();
				result+=entry.getKey()+"="+entry.getValue();
				if (iterator.hasNext()){
					result+=", ";
				}
			}
		} else {
			result = String.valueOf(context.getResult());
		}

		if (jobException != null) {

			String errMsg = jobException.getMessage();
			args = new Object[] { 
					context.getJobDetail().getKey().getName(),
					context.getJobDetail().getKey().getGroup(),
					new java.util.Date(),
					trigger.getKey().getName(),
					trigger.getKey().getGroup(),
					trigger.getPreviousFireTime(),
					trigger.getNextFireTime(),
					Integer.valueOf(context.getRefireCount()),
					context.getFireInstanceId(),
					result,
					errMsg };

			try {
				insertJobDetail(this.getNonManagedTXConnection(), context);
				getLog().info(MessageFormat.format(getJobFailedMessage(), args)); // without exception trace
//				getLog().info(MessageFormat.format(getJobFailedMessage(), args),
//				jobException);
			} catch (Exception e) {
				getLog().error(MessageFormat.format(getJobFailedMessage(), args, e));
			}
		} else {

			args = new Object[] { 
					context.getJobDetail().getKey().getName(),
					context.getJobDetail().getKey().getGroup(),
					new java.util.Date(),
					trigger.getKey().getName(),
					trigger.getKey().getGroup(),
					trigger.getPreviousFireTime(),
					trigger.getNextFireTime(),
					Integer.valueOf(context.getRefireCount()),
					context.getFireInstanceId(),
					result };

			try {
				insertJobDetail(this.getNonManagedTXConnection(), context);
				getLog().info(MessageFormat.format(getJobSuccessMessage(),	args));
			} catch (Exception e) {
				getLog().error(MessageFormat.format(getJobSuccessMessage(), args, e));
			}
		}
	}

	/**
	 * @see org.quartz.JobListener#jobExecutionVetoed(org.quartz.JobExecutionContext)
	 */
	public void jobExecutionVetoed(JobExecutionContext context) {

		Trigger trigger = context.getTrigger();

		Object[] args = { context.getJobDetail().getKey().getName(),
				context.getJobDetail().getKey().getGroup(),
				new java.util.Date(), trigger.getKey().getName(),
				trigger.getKey().getGroup(), trigger.getPreviousFireTime(),
				trigger.getNextFireTime(),
				Integer.valueOf(context.getRefireCount()) };
		
		try {
//TODO : not for jobExecutionVetoed ?
//			insertJobDetail(this.getNonManagedTXConnection(), context);
			getLog().info(MessageFormat.format(getJobWasVetoedMessage(), args));
		} catch (Exception e) {
			getLog().error(MessageFormat.format(getJobWasVetoedMessage(), args, e));
		}
	}

	private int insertJobDetail(Connection conn, JobExecutionContext context)
			throws IOException, SQLException, SchedulerException {

		JobDetail job = context.getJobDetail();
		JobDataMap jobDataMap = job.getJobDataMap();
		
		ByteArrayOutputStream baos = serializeJobData(job.getJobDataMap());

		PreparedStatement ps = null;

		int insertResult = 0;

		try {
			ps = conn.prepareStatement(rtp(INSERT_JOB_DETAIL_TRACK, scheduler.getSchedulerName()));
			ps.setString(1, job.getKey().getName());
			ps.setString(2, job.getKey().getGroup());

            String instanceId = context.getFireInstanceId();
			ps.setString(3, instanceId);

			ps.setString(4, job.getDescription());
			ps.setString(5, job.getJobClass().getName());
			//TODO verify it works with DB2v8
			// DB2v8 : ps.setInt(6, ((job.isDurable()) ? 1 : 0));
			ps.setBoolean(6, job.isDurable());
			//TODO verify it works with DB2v8
			// DB2v8 : ps.setInt(7, ((job.isConcurrentExectionDisallowed()) ? 1 : 0));
			ps.setBoolean(7, job.isConcurrentExectionDisallowed());
			//TODO verify it works with DB2v8
			// DB2v8 : ps.setInt(8, ((job.isPersistJobDataAfterExecution()) ? 1 : 0));
			ps.setBoolean(8, job.isPersistJobDataAfterExecution());
			//TODO verify it works with DB2v8
			// DB2v8 : ps.setInt(9, ((job.requestsRecovery()) ? 1 : 0));
			ps.setBoolean(9, job.requestsRecovery());
			//TODO verify it works with Sybase
	        // Sybase : ps.setBytes(10, (baos == null) ? null: baos.toByteArray());
			ps.setBytes(10, (baos == null) ? new byte[0] : baos.toByteArray());
			ps.setObject(11, context.getResult());
			String restartedInstanceId = jobDataMap.getString(RESTARTED_FIRE_INSTANCE_ID);
			ps.setString(12, restartedInstanceId);

			String returnCode = null;
			Object oResult = context.getResult();
			if (oResult instanceof JobDataMap) {
				returnCode = ((JobDataMap)oResult).getString(QuartzContextAdapter.RETURN_CODE);
			} else {
				returnCode = String.valueOf(context.getResult());
			}
			ps.setString(13, returnCode);


			insertResult = ps.executeUpdate();
		} finally {
			closeStatement(ps);
			cleanupConnection(conn);
		}
        return insertResult;
	}

}
