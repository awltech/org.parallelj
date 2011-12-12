package org.parallelj.tracknrestart.listeners;

import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;


import org.parallelj.tracknrestart.aspects.TrackNRestartException;
import org.parallelj.tracknrestart.jdbc.JDBCSupport;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobPersistenceException;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.jdbcjobstore.Constants;

public class TrackNRestartListener extends JDBCSupport implements ForEachListener {

	/**
	 * 
	 */
	static final long serialVersionUID = 1L;

	private String name;

    private String schedulerName;

	private String groupName;

	private String jobName;  
	 
	private String fireInstanceId;  
	 
	private String iterateCompleteMessage = "Job {1}.{0} iteration id {2} complete with ";

	private String iterateFetchedMessage = "Job {1}.{0} fetched iteration id {2} with status ";

	private String COL_ITERID_SUBST = "ITERID";

	private String COL_SUCCESS_SUBST = "SUCCESS";

    private String INSERT_ITERATION_TRACK = "INSERT INTO "
        + TABLE_PREFIX_SUBST + "ITERATIONS" 
        + " (" 
        + Constants.COL_SCHEDULER_NAME + ", " 
        + Constants.COL_JOB_NAME + ", " 
        + Constants.COL_JOB_GROUP + ", "
        + COL_UID_SUBST + ", " 
        + COL_ITERID_SUBST + ", " 
        + COL_SUCCESS_SUBST 
        + ") " 
        + " VALUES(" + SCHED_NAME_SUBST + ", ?, ?, ?, ?, ?)";

    private String FETCH_ITERATION_TRACK = "SELECT " + COL_SUCCESS_SUBST + " FROM "
        + TABLE_PREFIX_SUBST + "ITERATIONS" 
        + " WHERE " 
        + Constants.COL_SCHEDULER_NAME + " = "+ SCHED_NAME_SUBST +" AND "
        + Constants.COL_JOB_NAME + " = ? AND "
        + Constants.COL_JOB_GROUP + " = ? AND "
        + COL_UID_SUBST + " = ? AND "
        + COL_ITERID_SUBST + " = ?";

	public TrackNRestartListener(String dataSource, String tablePrefix, String name, String schedulerName, String groupName, String jobName, String fireInstanceId) {
		this.dataSource = dataSource;
		this.tablePrefix = tablePrefix;
		this.name = name;
		this.schedulerName = schedulerName;
		this.groupName = groupName;
		this.jobName = jobName;
		this.fireInstanceId = fireInstanceId;
        getLog().info("Creating Track Restart Listener.");
	}

	public String getIterateCompleteMessage() {
		return iterateCompleteMessage;
	}

	public String getIterateFetchedMessage() {
		return iterateFetchedMessage;
	}

	public void setIterateCompleteMessage(String iterateCompleteMessage) {
		this.iterateCompleteMessage = iterateCompleteMessage;
	}

	public String getName() {
		return name;
	}

	public String getGroupName() {
		return groupName;
	}

	public String getJobName() {
		return jobName;
	}

	public String getFireInstanceId() {
		return fireInstanceId;
	}

	public void forEachInstanceComplete(String oid, boolean success) throws JobPersistenceException, SQLException {
		Object[] args = null;

		args = new Object[] {jobName, groupName, oid};

//		try {
			insertIteration(this.getNonManagedTXConnection(), oid, success);
			getLog().debug(MessageFormat.format(getIterateCompleteMessage()+(success==true?"SUCCESS":"FAILURE"), args));
//		} catch (Exception e) {
//			getLog().error("Unable to persist iteration '" + oid + "' status");
//			throw e;
//		}
	}

	private int insertIteration(Connection conn, String oid, boolean success) throws SQLException {

//		JobDetail job = context.getJobDetail();

		PreparedStatement ps = null;

		int insertResult = 0;

		try {
			ps = conn.prepareStatement(rtp(INSERT_ITERATION_TRACK, schedulerName));
			ps.setString(1, jobName);
			ps.setString(2, groupName);
			ps.setString(3, fireInstanceId);

			ps.setString(4, oid);
			ps.setBoolean(5, success);
			insertResult = ps.executeUpdate();
		} finally {
			closeStatement(ps);
			cleanupConnection(conn);
		}
        return insertResult;
	}

	public boolean isForEachInstanceIgnorable(String restartedFireInstanceId, String oid) throws SQLException, JobPersistenceException {

		Object[] args = null;

		args = new Object[] { jobName, groupName, oid};

		boolean status = false;
//		try {
			status = fetchIteration(this.getNonManagedTXConnection(), restartedFireInstanceId, oid);
			getLog().debug(MessageFormat.format(getIterateFetchedMessage()+(status==true?"SUCCESS":"FAILURE"), args));
//		} catch (Exception e) {
//			throw new TrackNRestartException("Unable to fetch iteration '"+oid+"'", e);
//		}
		return status;
	}

	private boolean fetchIteration(Connection conn, 
			String restartedFireInstanceId, String oid) throws SQLException {

		PreparedStatement ps = null;
		ResultSet rs = null;
		boolean iterationSucceeded = false;
		
		try {
			ps = conn.prepareStatement(rtp(FETCH_ITERATION_TRACK, this.schedulerName));
			ps.setString(1, jobName);
			ps.setString(2, groupName);
			ps.setString(3, restartedFireInstanceId);
			ps.setString(4, oid);
			rs = ps.executeQuery();
			if (rs.next()) {
				iterationSucceeded = rs.getBoolean(1);
			}
		} finally {
			closeStatement(ps);
			cleanupConnection(conn);
		}
		return iterationSucceeded;
	}

}
