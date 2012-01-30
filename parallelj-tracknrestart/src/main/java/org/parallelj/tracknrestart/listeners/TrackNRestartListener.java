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
package org.parallelj.tracknrestart.listeners;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;

import org.parallelj.tracknrestart.ReturnCodes;
//import org.parallelj.tracknrestart.jdbc.JDBCSupport;
import org.parallelj.tracknrestart.jdbc.JDBCSupport;
import org.quartz.JobPersistenceException;
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
        getLog().debug("Creating Track Restart Listener.");
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

		insertIteration(this.getNonManagedTXConnection(), oid, success);
		getLog().debug(MessageFormat.format(getIterateCompleteMessage()+(success?ReturnCodes.SUCCESS:ReturnCodes.FAILURE), args));
	}

	private int insertIteration(Connection conn, String oid, boolean success) throws SQLException {

		PreparedStatement ps = null;

		int insertResult = 0;

		try {
			ps = conn.prepareStatement(rtp(INSERT_ITERATION_TRACK, schedulerName));
			ps.setString(1, jobName);
			ps.setString(2, groupName);
			ps.setString(3, fireInstanceId);

			ps.setString(4, oid);
			//TODO verify it works with DB2v8
			// DB2v8 : ps.setInt(5, ((success) ? 1 : 0));
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
		status = fetchIteration(this.getNonManagedTXConnection(), restartedFireInstanceId, oid);
		getLog().debug(MessageFormat.format(getIterateFetchedMessage()+(status?ReturnCodes.SUCCESS:ReturnCodes.FAILURE), args));

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