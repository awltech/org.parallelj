package org.parallelj.tracknrestart.listeners;

import java.io.IOException;
import java.io.Serializable;
import java.sql.SQLException;

import org.quartz.JobExecutionContext;
import org.quartz.JobPersistenceException;
import org.quartz.SchedulerException;

public interface ForEachListener extends Serializable {

    void forEachInstanceComplete(String oid, boolean success) throws JobPersistenceException, SQLException;

    boolean isForEachInstanceIgnorable(String restartedFireInstanceId, String oid) throws JobPersistenceException, SQLException;
}
