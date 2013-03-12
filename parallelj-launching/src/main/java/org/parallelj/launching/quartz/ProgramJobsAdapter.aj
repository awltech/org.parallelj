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

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.parallelj.Programs;
import org.parallelj.Programs.ProcessHelper;
import org.parallelj.internal.kernel.KProgram;
import org.parallelj.internal.reflect.ProgramAdapter.Adapter;
import org.parallelj.launching.LaunchingMessageKind;
import org.parallelj.launching.ProgramReturnCodes;
import org.parallelj.launching.internal.LaunchingObservable;
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
privileged public aspect ProgramJobsAdapter {

	/*
	 * The Aspect JobsAdapter must be passed before this.
	 */
	declare precedence :
		org.parallelj.internal.kernel.Identifiers,
		org.parallelj.internal.reflect.ProgramAdapter,
		org.parallelj.internal.util.sm.impl.KStateMachines,
		org.parallelj.internal.util.sm.impl.KStateMachines.PerMachine,
		org.parallelj.Executables$PerExecutable,
		org.parallelj.internal.reflect.ProgramAdapter.PerProgram,
		org.parallelj.internal.log.Logs,
		org.parallelj.launching.quartz.JobsAdapter;

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
		JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
		context.setResult(jobDataMap);
		jobDataMap.put(QuartzUtils.RETURN_CODE, ProgramReturnCodes.SUCCESS);

		proceed(self, context);

		try {
			// Initialize an ExecutorService with the Capacity of the Program
			ProcessHelper<?> processHelper = Programs.as((Adapter) self);
			if (processHelper == null) {
				jobDataMap.put(QuartzUtils.RETURN_CODE, ProgramReturnCodes.NOTSTARTED);
				throw new JobExecutionException(LaunchingMessageKind.ELAUNCH0003.getFormatedMessage(self));
			}
			ProgramType programType = processHelper.getProcess().getProgram();

			LaunchingObservable observable = new LaunchingObservable();
			observable.prepareLaunching((Adapter) self, processHelper, context);
			
			/*
			 * ExecutorService
			 * 
			 * If an executorService was specified, we use it
			 */
			ExecutorService service = null;
			if (context.getJobDetail().getJobDataMap().get(Launch.DEFAULT_EXECUTOR_KEY) != null) {
				service = (ExecutorService)context.getJobDetail().getJobDataMap().get(Launch.DEFAULT_EXECUTOR_KEY);
			} else 
			if (programType instanceof KProgram) {
				// Initialize an ExecutorService with the Program Capacity
				short programCapacity = ((KProgram) programType)
						.getCapacity();
				service = (programCapacity == Short.MAX_VALUE) ? Executors
						.newCachedThreadPool() : Executors
						.newFixedThreadPool(programCapacity);
			} else {
				service = Executors
						.newCachedThreadPool();
			}
			
			/*
			 *  Launch the program with the initialized ExecutorService
			 */
			processHelper.execute(service).join();
			jobDataMap.put(QuartzUtils.CONTEXT, processHelper);
			
			observable.finalizeLaunching((Adapter) self, processHelper, context);
			service.shutdown();
			
		} catch (Exception e) {
			jobDataMap.put(QuartzUtils.RETURN_CODE, ProgramReturnCodes.FAILURE);
			throw new JobExecutionException(e);
		}
	}
	
}
