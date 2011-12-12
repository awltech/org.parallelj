package org.parallelj.tracknrestart.test;

import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobListener;
import org.quartz.Scheduler;

public class TestListener implements JobListener {

	private String name;
	private Scheduler scheduler;
	private JobDataMap result = null;

	public TestListener(String name, Scheduler scheduler) {
		super();
		this.name = name;
		this.scheduler = scheduler;
	}

	public JobDataMap getResult() {
		return result;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setScheduler(Scheduler scheduler) {
		this.scheduler = scheduler;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void jobToBeExecuted(JobExecutionContext context) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void jobExecutionVetoed(JobExecutionContext context) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void jobWasExecuted(JobExecutionContext context,
			JobExecutionException jobException) {
		result = (JobDataMap) context.getResult();
	}

}
