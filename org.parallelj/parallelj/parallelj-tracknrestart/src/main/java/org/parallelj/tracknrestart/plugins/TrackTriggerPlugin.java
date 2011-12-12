package org.parallelj.tracknrestart.plugins;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.MessageFormat;


import org.parallelj.tracknrestart.jdbc.JDBCSupport;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.Scheduler;
import org.quartz.SchedulerConfigException;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.Trigger.CompletedExecutionInstruction;
import org.quartz.TriggerListener;
import org.quartz.impl.jdbcjobstore.Constants;
import org.quartz.impl.matchers.EverythingMatcher;
import org.quartz.spi.OperableTrigger;
import org.quartz.spi.SchedulerPlugin;

public class TrackTriggerPlugin extends JDBCSupport implements SchedulerPlugin, TriggerListener {

	private String name;

    private Scheduler scheduler;

    private String triggerFiredMessage = "Trigger {1}.{0} fired job {6}.{5} at: {4, date, HH:mm:ss MM/dd/yyyy}";

	private String triggerMisfiredMessage = "Trigger {1}.{0} misfired job {6}.{5}  at: {4, date, HH:mm:ss MM/dd/yyyy}.  Should have fired at: {3, date, HH:mm:ss MM/dd/yyyy}";

	private String triggerCompleteMessage = "Trigger {1}.{0} completed firing job {6}.{5} at {4, date, HH:mm:ss MM/dd/yyyy} with resulting trigger instruction code: {9}";

	String INSERT_TRIGGER_TRACK = "INSERT INTO " + TABLE_PREFIX_SUBST
			+ Constants.TABLE_TRIGGERS + " (" + Constants.COL_SCHEDULER_NAME
			+ ", " + Constants.COL_TRIGGER_NAME + ", "
			+ Constants.COL_TRIGGER_GROUP + ", " + COL_UID_SUBST + ", " + Constants.COL_JOB_NAME
			+ ", " + Constants.COL_JOB_GROUP + ", "
			+ Constants.COL_DESCRIPTION + ", " + Constants.COL_NEXT_FIRE_TIME
			+ ", " + Constants.COL_PREV_FIRE_TIME + ", "
			+ Constants.COL_TRIGGER_STATE + ", " + Constants.COL_TRIGGER_TYPE
			+ ", " + Constants.COL_START_TIME + ", " + Constants.COL_END_TIME
			+ ", " + Constants.COL_CALENDAR_NAME + ", "
			+ Constants.COL_MISFIRE_INSTRUCTION + ", "
			+ Constants.COL_JOB_DATAMAP + ", " + Constants.COL_PRIORITY + ") "
			+ " VALUES(" + SCHED_NAME_SUBST
			+ ", ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

//	String INSERT_SIMPLE_TRIGGER_TRACK = "INSERT INTO " + TABLE_PREFIX_SUBST
//			+ Constants.TABLE_SIMPLE_TRIGGERS + " ("
//			+ Constants.COL_SCHEDULER_NAME + ", " + Constants.COL_TRIGGER_NAME
//			+ ", " + Constants.COL_TRIGGER_GROUP + ", " + COL_UID_SUBST + ", "
//			+ Constants.COL_REPEAT_COUNT + ", " + Constants.COL_REPEAT_INTERVAL
//			+ ", " + Constants.COL_TIMES_TRIGGERED + ") " + " VALUES("
//			+ SCHED_NAME_SUBST + ", ?, ?, ?, ?, ?, ?)";

    String INSERT_BLOB_TRIGGER_TRACK = "INSERT INTO "
        + TABLE_PREFIX_SUBST + Constants.TABLE_BLOB_TRIGGERS + " ("
        + Constants.COL_SCHEDULER_NAME + ", "
        + Constants.COL_TRIGGER_NAME + ", " + Constants.COL_TRIGGER_GROUP + ", " + COL_UID_SUBST + ", " + Constants.COL_BLOB
        + ") " + " VALUES(" + SCHED_NAME_SUBST + ", ?, ?, ?, ?)";

	public TrackTriggerPlugin() {
	}

	/**
	 * Get the message that is printed upon the completion of a trigger's
	 * firing.
	 * 
	 * @return String
	 */
	public String getTriggerCompleteMessage() {
		return triggerCompleteMessage;
	}

	/**
	 * Get the message that is printed upon a trigger's firing.
	 * 
	 * @return String
	 */
	public String getTriggerFiredMessage() {
		return triggerFiredMessage;
	}

	/**
	 * Get the message that is printed upon a trigger's mis-firing.
	 * 
	 * @return String
	 */
	public String getTriggerMisfiredMessage() {
		return triggerMisfiredMessage;
	}

	/**
	 * Set the message that is printed upon the completion of a trigger's
	 * firing.
	 * 
	 * @param triggerCompleteMessage
	 *            String in java.text.MessageFormat syntax.
	 */
	public void setTriggerCompleteMessage(String triggerCompleteMessage) {
		this.triggerCompleteMessage = triggerCompleteMessage;
	}

	/**
	 * Set the message that is printed upon a trigger's firing.
	 * 
	 * @param triggerFiredMessage
	 *            String in java.text.MessageFormat syntax.
	 */
	public void setTriggerFiredMessage(String triggerFiredMessage) {
		this.triggerFiredMessage = triggerFiredMessage;
	}

	/**
	 * Set the message that is printed upon a trigger's firing.
	 * 
	 * @param triggerMisfiredMessage
	 *            String in java.text.MessageFormat syntax.
	 */
	public void setTriggerMisfiredMessage(String triggerMisfiredMessage) {
		this.triggerMisfiredMessage = triggerMisfiredMessage;
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
		scheduler.getListenerManager().addTriggerListener(this,
				EverythingMatcher.allTriggers());
        getLog().info("Registering Quartz Trigger Tracking Plug-in.");
		
//        addDefaultTriggerPersistenceDelegates(scheduler.getSchedulerName());
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

	public String getName() {
		return name;
	}

	public void triggerFired(Trigger trigger, JobExecutionContext context) {
		if (!getLog().isInfoEnabled()) {
			return;
		}

		Object[] args = { trigger.getKey().getName(),
				trigger.getKey().getGroup(), trigger.getPreviousFireTime(),
				trigger.getNextFireTime(), new java.util.Date(),
				context.getJobDetail().getKey().getName(),
				context.getJobDetail().getKey().getGroup(),
				Integer.valueOf(context.getRefireCount()) };

		getLog().info(MessageFormat.format(getTriggerFiredMessage(), args));
	}

	public void triggerMisfired(Trigger trigger) {
		if (!getLog().isInfoEnabled()) {
			return;
		}

		Object[] args = { trigger.getKey().getName(),
				trigger.getKey().getGroup(), trigger.getPreviousFireTime(),
				trigger.getNextFireTime(), new java.util.Date(),
				trigger.getJobKey().getName(), trigger.getJobKey().getGroup() };

		getLog().info(MessageFormat.format(getTriggerMisfiredMessage(), args));
	}

	public void triggerComplete(Trigger trigger, JobExecutionContext context,
			CompletedExecutionInstruction triggerInstructionCode) {
		if (!getLog().isInfoEnabled()) {
			return;
		}

		String instrCode = "UNKNOWN";
		if (triggerInstructionCode == CompletedExecutionInstruction.DELETE_TRIGGER) {
			instrCode = "DELETE TRIGGER";
		} else if (triggerInstructionCode == CompletedExecutionInstruction.NOOP) {
			instrCode = "DO NOTHING";
		} else if (triggerInstructionCode == CompletedExecutionInstruction.RE_EXECUTE_JOB) {
			instrCode = "RE-EXECUTE JOB";
		} else if (triggerInstructionCode == CompletedExecutionInstruction.SET_ALL_JOB_TRIGGERS_COMPLETE) {
			instrCode = "SET ALL OF JOB'S TRIGGERS COMPLETE";
		} else if (triggerInstructionCode == CompletedExecutionInstruction.SET_TRIGGER_COMPLETE) {
			instrCode = "SET THIS TRIGGER COMPLETE";
		}

		Object[] args = { trigger.getKey().getName(),
				trigger.getKey().getGroup(), trigger.getPreviousFireTime(),
				trigger.getNextFireTime(), new java.util.Date(),
				context.getJobDetail().getKey().getName(),
				context.getJobDetail().getKey().getGroup(),
				Integer.valueOf(context.getRefireCount()),
				triggerInstructionCode.toString(), instrCode };

		try {
			insertTrigger(this.getNonManagedTXConnection(), (OperableTrigger)trigger, instrCode, context);
			getLog().debug(MessageFormat.format(getTriggerCompleteMessage(), args));
		} catch (Exception e) {
			getLog().error(MessageFormat.format(getTriggerCompleteMessage(), args, e));
		}
	}

	public boolean vetoJobExecution(Trigger trigger, JobExecutionContext context) {
		return false;
	}

    private int insertTrigger(Connection conn, OperableTrigger trigger, String state,
    		JobExecutionContext context) throws SQLException, IOException, SchedulerException {

		JobDetail jobDetail = context.getJobDetail();

		ByteArrayOutputStream baos = null;

		if(trigger.getJobDataMap().size() > 0) {
            baos = serializeJobData(trigger.getJobDataMap());
        }
        
        PreparedStatement ps = null;

        int insertResult = 0;

        try {
            ps = conn.prepareStatement(rtp(INSERT_TRIGGER_TRACK, scheduler.getSchedulerName()));
            ps.setString(1, trigger.getKey().getName());
            ps.setString(2, trigger.getKey().getGroup());

            String instanceId = context.getFireInstanceId();
            ps.setString(3, instanceId);

            ps.setString(4, trigger.getJobKey().getName());
            ps.setString(5, trigger.getJobKey().getGroup());
            ps.setString(6, trigger.getDescription());
            if(trigger.getNextFireTime() != null)
	            ps.setBigDecimal(7, new BigDecimal(String.valueOf(trigger
	                    .getNextFireTime().getTime())));
            else
            	ps.setBigDecimal(7, null);
            long prevFireTime = -1;
            if (trigger.getPreviousFireTime() != null) {
                prevFireTime = trigger.getPreviousFireTime().getTime();
            }
            ps.setBigDecimal(8, new BigDecimal(String.valueOf(prevFireTime)));
            ps.setString(9, state);

//TODO            
//            TriggerPersistenceDelegate tDel = findTriggerPersistenceDelegate(trigger);
//            
//            String type = Constants.TTYPE_BLOB;
//            if(tDel != null)
//                type = tDel.getHandledTriggerTypeDiscriminator();
//          ps.setString(10, type);
            ps.setString(10, "UNKNOWN");  //en attendant !
            
            ps.setBigDecimal(11, new BigDecimal(String.valueOf(trigger
                    .getStartTime().getTime())));
            long endTime = 0;
            if (trigger.getEndTime() != null) {
                endTime = trigger.getEndTime().getTime();
            }
            ps.setBigDecimal(12, new BigDecimal(String.valueOf(endTime)));
            ps.setString(13, trigger.getCalendarName());
            ps.setInt(14, trigger.getMisfireInstruction());
			ps.setBytes(15, (baos == null) ? new byte[0] : baos.toByteArray());
            ps.setInt(16, trigger.getPriority());
            
            insertResult = ps.executeUpdate();
            
//TODO            
//            if(tDel == null)
//                insertBlobTrigger(conn, trigger, context);
//            else
//                tDel.insertExtendedTriggerProperties(conn, trigger, state, jobDetail);
            insertBlobTrigger(conn, trigger, context); //
            
        } finally {
            closeStatement(ps);
			cleanupConnection(conn);
        }

        return insertResult;
    }

	private int insertBlobTrigger(Connection conn, OperableTrigger trigger, JobExecutionContext context)
			throws SQLException, IOException, SchedulerException {
		PreparedStatement ps = null;
		ByteArrayOutputStream os = null;

		try {
			// update the blob
			os = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(os);
			oos.writeObject(trigger);
			oos.close();

			byte[] buf = os.toByteArray();
			ByteArrayInputStream is = new ByteArrayInputStream(buf);

			ps = conn.prepareStatement(rtp(INSERT_BLOB_TRIGGER_TRACK, scheduler.getSchedulerName()));
			ps.setString(1, trigger.getKey().getName());
			ps.setString(2, trigger.getKey().getGroup());

            String instanceId = context.getFireInstanceId();
			ps.setString(3, instanceId);

			ps.setBinaryStream(4, is, buf.length);

			return ps.executeUpdate();
		} finally {
			closeStatement(ps);
//			cleanupConnection(conn);
		}
	}

}
