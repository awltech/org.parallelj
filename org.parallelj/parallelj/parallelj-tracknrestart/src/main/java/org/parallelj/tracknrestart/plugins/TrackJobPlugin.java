package org.parallelj.tracknrestart.plugins;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.MessageFormat;

import org.parallelj.tracknrestart.jdbc.JDBCSupport;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobListener;
import org.quartz.Scheduler;
import org.quartz.SchedulerConfigException;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.impl.jdbcjobstore.Constants;
import org.quartz.impl.matchers.EverythingMatcher;
import org.quartz.spi.SchedulerPlugin;

@Deprecated
public class TrackJobPlugin extends JDBCSupport implements SchedulerPlugin, JobListener {

	private String name;

    private Scheduler scheduler;

    private String jobToBeFiredMessage = "Job {1}.{0} [id #{8}] fired (by trigger {4}.{3}) at: {2, date, HH:mm:ss MM/dd/yyyy}";

	private String jobSuccessMessage = "Job {1}.{0} [id #{8}] execution complete at {2, date, HH:mm:ss MM/dd/yyyy} and reports: {9}";

	private String jobFailedMessage = "Job {1}.{0}  [id #{8}] execution failed at {2, date, HH:mm:ss MM/dd/yyyy} and reports: {9}";

	private String jobWasVetoedMessage = "Job {1}.{0} was vetoed.  It was to be fired (by trigger {4}.{3}) at: {2, date, HH:mm:ss MM/dd/yyyy}";

	private String COL_RESULT_SUBST = "RESULT";

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
        + COL_RESULT_SUBST 
        + ") " 
        + " VALUES(" + SCHED_NAME_SUBST + ", ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

	public TrackJobPlugin() {
	}

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
	 * Set the message that is logged when a Job successfully completes its
	 * execution.
	 * 
	 * @param jobSuccessMessage
	 *            String in java.text.MessageFormat syntax.
	 */
	public void setJobSuccessMessage(String jobSuccessMessage) {
		this.jobSuccessMessage = jobSuccessMessage;
	}

	/**
	 * Set the message that is logged when a Job fails its execution.
	 * 
	 * @param jobFailedMessage
	 *            String in java.text.MessageFormat syntax.
	 */
	public void setJobFailedMessage(String jobFailedMessage) {
		this.jobFailedMessage = jobFailedMessage;
	}

	/**
	 * Set the message that is logged when a Job is about to execute.
	 * 
	 * @param jobToBeFiredMessage
	 *            String in java.text.MessageFormat syntax.
	 */
	public void setJobToBeFiredMessage(String jobToBeFiredMessage) {
		this.jobToBeFiredMessage = jobToBeFiredMessage;
	}

	/**
	 * Get the message that is logged when a Job execution is vetoed by a
	 * trigger listener.
	 */
	public String getJobWasVetoedMessage() {
		return jobWasVetoedMessage;
	}

	/**
	 * Set the message that is logged when a Job execution is vetoed by a
	 * trigger listener.
	 * 
	 * @param jobWasVetoedMessage
	 *            String in java.text.MessageFormat syntax.
	 */
	public void setJobWasVetoedMessage(String jobWasVetoedMessage) {
		this.jobWasVetoedMessage = jobWasVetoedMessage;
	}

	/**
	 * <p>
	 * Called during creation of the <code>Scheduler</code> in order to give the
	 * <code>SchedulerPlugin</code> a chance to initialize.
	 * </p>
	 * 
	 * @throws SchedulerConfigException
	 *             if there is an error initializing.
	 */
	public void initialize(String pname, Scheduler scheduler)
			throws SchedulerException {
		this.name = pname;
		this.scheduler = scheduler;
		scheduler.getListenerManager().addJobListener(this,
				EverythingMatcher.allJobs());
        getLog().info("Registering Quartz Job Tracking Plug-in.");
	}

	public void start() {
		// do nothing...
	}

	/**
	 * <p>
	 * Called in order to inform the <code>SchedulerPlugin</code> that it should
	 * free up all of it's resources because the scheduler is shutting down.
	 * </p>
	 */
	public void shutdown() {
		// nothing to do...
	}

	/*
	 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	 * 
	 * JobListener Interface.
	 * 
	 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	 */

	public String getName() {
		return name;
	}

	/**
	 * @see org.quartz.JobListener#jobToBeExecuted(JobExecutionContext)
	 */
	public void jobToBeExecuted(JobExecutionContext context) {
		
		Trigger trigger = context.getTrigger();

		Object[] args = { 
				context.getJobDetail().getKey().getName(),
				context.getJobDetail().getKey().getGroup(),
				new java.util.Date(),
				trigger.getKey().getName(),
				trigger.getKey().getGroup(),
				trigger.getPreviousFireTime(),
				trigger.getNextFireTime(),
				Integer.valueOf(context.getRefireCount()),
				context.getFireInstanceId()
				};

		try {
			JobDetail jobDetail = context.getJobDetail();
			JobDataMap jobDataMap = jobDetail.getJobDataMap();
//			this.scheduler.addJob(jobDetail, true);

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
					errMsg };

			try {
				insertJobDetail(this.getNonManagedTXConnection(), context);
				getLog().info(MessageFormat.format(getJobFailedMessage(), args),
						jobException);
			} catch (Exception e) {
				getLog().error(MessageFormat.format(getJobFailedMessage(), args, e));
			}
		} else {

			String result = String.valueOf(context.getResult());
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
			ps.setBoolean(6, job.isDurable());
			ps.setBoolean(7, job.isConcurrentExectionDisallowed());
			ps.setBoolean(8, job.isPersistJobDataAfterExecution());
			ps.setBoolean(9, job.requestsRecovery());
			ps.setBytes(10, (baos == null) ? new byte[0] : baos.toByteArray());
			ps.setObject(11, context.getResult());
			insertResult = ps.executeUpdate();
		} finally {
			closeStatement(ps);
			cleanupConnection(conn);
		}
        return insertResult;
	}

}
