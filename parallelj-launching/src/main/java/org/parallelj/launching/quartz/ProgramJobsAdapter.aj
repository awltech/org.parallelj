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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.parallelj.Programs;
import org.parallelj.Programs.ProcessHelper;
import org.parallelj.internal.kernel.KCall;
import org.parallelj.internal.kernel.KProgram;
import org.parallelj.internal.reflect.ProgramAdapter;
import org.parallelj.internal.reflect.ProgramAdapter.Adapter;
import org.parallelj.launching.LaunchingMessageKind;
import org.parallelj.launching.ReturnCodes;
import org.parallelj.mirror.ProgramType;
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
		org.parallelj.internal.reflect.ProgramAdapter,
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
				// Initialize an ExecutorService with the Capacity of the Program
				ProcessHelper<?> processHelper = Programs.as((Adapter) self);
				if (processHelper == null) {
					jobDataMap.put(QuartzUtils.RETURN_CODE, ReturnCodes.NOTSTARTED);
					throw new JobExecutionException(LaunchingMessageKind.ELAUNCH0003.getFormatedMessage(self));
				}
				ProgramType programType = processHelper.getProcess().getProgram();
				ExecutorService service = null;
				// If an executorService was specified, we use it
				if (context.getJobDetail().getJobDataMap().get(Launch.DEFAULT_EXECUTOR_KEY) != null) {
					service = (ExecutorService)context.getJobDetail().getJobDataMap().get(Launch.DEFAULT_EXECUTOR_KEY);
				} else 
				if (programType instanceof KProgram) {
					short programCapacity = ((KProgram) programType)
							.getCapacity();
					service = (programCapacity == Short.MAX_VALUE) ? Executors
							.newCachedThreadPool() : Executors
							.newFixedThreadPool(programCapacity);
				} else {
					service = Executors
							.newCachedThreadPool();
				}
				// Launch the program with the initialized ExecutorService
				processHelper.execute(service).join();
				service.shutdown();
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
	 * Intercept Exception thrown in RunnableProcedure/CallableProcedure for
	 * tracing. If an Exception is thrown, the return code of a Launch becomes
	 * FAILURE.
	 * 
	 * @param self
	 */
	pointcut enter(KCall _kCall): call(* org.parallelj.internal.kernel.callback.Entry+.enter(KCall)) && args(_kCall);

	pointcut invoke(): call(public Object Method.invoke(Object, ..)) && !within(ProgramJobsAdapter);

	after(Object oo, KCall _kCall) throwing (InvocationTargetException ite) : 
    	invoke() && args(oo, ..) && cflow(enter(_kCall)) {

		LaunchingMessageKind.ELAUNCH0002.format(this.adpater, ite);
		if (this.context != null && this.context.getResult() != null
				&& this.context.getResult() instanceof JobDataMap) {
			((JobDataMap) this.context.getResult()).put(
					QuartzUtils.RETURN_CODE, ReturnCodes.FAILURE);
		}
	}
}
