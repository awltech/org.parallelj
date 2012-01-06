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
package org.parallelj.launching.quartz;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.parallelj.Programs;
import org.parallelj.internal.kernel.KCall;
import org.parallelj.internal.reflect.ProgramAdapter.Adapter;
import org.parallelj.launching.LaunchingMessageKind;
import org.parallelj.launching.ReturnCodes;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * Implements the Job.execute(..) method
 * 
 * 
 */
privileged public aspect ProgramJobsAdapter percflow (execution(public void Job+.execute(..) throws JobExecutionException)) {

	/**
	 * The JobExecutionContext where get Program's arguments ant where to put
	 * the Result.
	 */
	public JobExecutionContext context;

	/*
	 * The Aspect JobsAdapter must be passed before this.
	 */
	declare precedence :
		org.parallelj.launching.quartz.JobsAdapter;

	private Adapter adpater;

	/**
	 * Launch a Program and initialize the Result as a JobDataMap.
	 * 
	 * @param self
	 * @param context
	 * @throws JobExecutionException
	 */
	void around(Job self, JobExecutionContext context)
			throws JobExecutionException : 
		execution( public void  Job+.execute(..) throws JobExecutionException) 
			&& (within(@org.parallelj.Program *) || within(JobsAdapter)) 
				&& args(context) && this(self) {
		this.adpater = (Adapter) self;
		this.context = context;
		JobDataMap jobDataMap = new JobDataMap();
		context.setResult(jobDataMap);
		jobDataMap.put(QuartzUtils.RETURN_CODE, ReturnCodes.SUCCESS);
		try {
			proceed(self, context);

			try {
				Programs.as((Adapter) self).execute().join();
				ProgramFieldsBinder.getProgramOutputFields(this, context);
			} catch (IllegalAccessException e) {
				jobDataMap.put(QuartzUtils.RETURN_CODE, ReturnCodes.FAILURE);
			} catch (NoSuchFieldException e) {
				jobDataMap.put(QuartzUtils.RETURN_CODE, ReturnCodes.FAILURE);
			}
		} catch (InvocationTargetException e) {
			jobDataMap.put(QuartzUtils.RETURN_CODE, ReturnCodes.FAILURE);
		}
	}

	/**
	 * Intercept Exception thrown in RunnableProcedure/CallableProcedure for tracing. If an
	 * Exception is thrown, the return code of a Launch becomes FAILURE.
	 * 
	 * @param self
	 */
    pointcut enter(KCall _kCall): call(* org.parallelj.internal.kernel.callback.Entry+.enter(KCall)) && args(_kCall);
    pointcut invoke(): call(public Object Method.invoke(Object, ..)) && !within(ProgramJobsAdapter);
    after(Object oo, KCall _kCall) throwing (InvocationTargetException ite) : 
    	invoke() && args(oo, ..) && cflow(enter(_kCall)) {
		
		LaunchingMessageKind.ELAUNCH0002.format(this.adpater, ite);
		((JobDataMap) this.context.getResult()).put(
				QuartzUtils.RETURN_CODE, ReturnCodes.FAILURE);
//		this.result = ReturnCodes.FAILURE.name();
//		track(_kCall, oo, false, ite);
    }
}
