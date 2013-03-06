package org.parallelj.launching.internal;

import org.parallelj.Programs.ProcessHelper;
import org.parallelj.internal.reflect.ProgramAdapter.Adapter;
import org.quartz.JobExecutionContext;

public abstract class AbstractLaunchingListener implements LaunchingListener {

	@Override
	public abstract void prepareLaunching(Adapter adapter, ProcessHelper<?> processHelper, JobExecutionContext context) throws Exception;

	@Override
	public abstract void finalizeLaunching(Adapter adapter, ProcessHelper<?> processHelper, JobExecutionContext context) throws Exception;

	@Override
	public abstract int getPriority();

}
