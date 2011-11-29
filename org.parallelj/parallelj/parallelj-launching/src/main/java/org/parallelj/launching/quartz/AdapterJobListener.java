package org.parallelj.launching.quartz;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobListener;

public class AdapterJobListener implements JobListener {

	private boolean isJobTerminated = false;
	private Object result=null;
	
	public boolean isJobTerminated() {
		return isJobTerminated;
	}

	public Object getResult() {
		return result;
	}

	@Override
	public String getName() {
        return this.toString();
	}

	@Override
	public void jobWasExecuted(JobExecutionContext context,
			JobExecutionException jobException) {
		this.isJobTerminated = true;
		this.result = context.getResult();
		System.out.println("====> "+context.getJobDetail().getJobClass()+" finished executing...");
	}

	@Override
	public void jobToBeExecuted(JobExecutionContext context) {
	}

	@Override
	public void jobExecutionVetoed(JobExecutionContext context) {
	}
}
