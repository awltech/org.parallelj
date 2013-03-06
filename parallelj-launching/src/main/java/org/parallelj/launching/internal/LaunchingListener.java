package org.parallelj.launching.internal;

import org.parallelj.Programs.ProcessHelper;
import org.parallelj.internal.reflect.ProgramAdapter.Adapter;
import org.quartz.JobExecutionContext;

public interface LaunchingListener {
	
	public void prepareLaunching(Adapter adapter, ProcessHelper<?> processHelper, JobExecutionContext context) throws Exception;
	
	public void finalizeLaunching(Adapter adapter, ProcessHelper<?> processHelper, JobExecutionContext context) throws Exception;
	
	public int getPriority();
}
