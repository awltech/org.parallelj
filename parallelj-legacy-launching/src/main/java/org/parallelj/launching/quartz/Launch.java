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

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;

import org.parallelj.launching.quartz.Launch;
import org.parallelj.launching.quartz.LaunchException;
import org.parallelj.launching.quartz.LaunchResult;
import org.quartz.Job;
import org.quartz.JobDataMap;

/**
 * The legacy launch entry of a Program.
 * Only for compatibility with projects using previous release...
 * 
 */
@Deprecated
public class Launch {

	public static final String DEFAULT_EXECUTOR_KEY = "EXECUTOR";
	public static final String PARAMETERS = "parameters";
	public static final String OUTPUTS = "outputs";

	/**
	 * The result Object of this Launch.
	 */
	private LaunchResult legacyLaunchResult;
	
	private org.parallelj.launching.Launch launch;

	/**
	 * Default Constructor.
	 * 
	 * @param scheduler
	 *            The ParalleljScheduler
	 * @param jobClass
	 *            The Program Adapter class
	 * @throws LaunchException
	 */
	public Launch(final Class<?> jobClass) throws LaunchException {
		try {
			this.launch = new org.parallelj.launching.Launch(jobClass);
		} catch (org.parallelj.launching.LaunchException e) {
			throw new LaunchException(e);
		}
	}

	public Launch(final Class<?> jobClass, ExecutorService executorService)
			throws LaunchException {
		try {
			this.launch = new org.parallelj.launching.Launch(jobClass, executorService);
		} catch (org.parallelj.launching.LaunchException e) {
			throw new LaunchException(e);
		}
	}

	/**
	 * Launch a Program and wait until it's terminated.
	 * 
	 * @return A Launch instance.
	 * @throws LaunchException
	 *             When a SchedulerException occurred.
	 */
	public Launch synchLaunch() throws LaunchException {
		JobDataMap jobDataMap = new JobDataMap();
		jobDataMap.put(Launch.PARAMETERS, this.launch.getInputParameters());
		jobDataMap.put(Launch.DEFAULT_EXECUTOR_KEY, this.launch.getExecutorService());
		try {
			this.launch.synchLaunch();
		} catch (org.parallelj.launching.LaunchException e) {
			throw new LaunchException(e);
		}
		
		org.parallelj.launching.LaunchResult result = this.launch.getLaunchResult();
		jobDataMap.put(QuartzUtils.RETURN_CODE, result.getStatusCode());
		jobDataMap.put(QuartzUtils.USER_RETURN_CODE, result.getReturnCode());
		jobDataMap.put(QuartzUtils.PROCEDURES_IN_ERROR, result.getProceduresInError());
		jobDataMap.put(Launch.OUTPUTS, this.launch.getLaunchResult().getOutputParameters());
		this.legacyLaunchResult = new LaunchResult(launch.getLaunchId(), jobDataMap);
		
		return this;
	}

	/**
	 * Launch a Program and continue.
	 * 
	 * @return A Launch instance.
	 * @throws LaunchException
	 *             When a SchedulerException occurred.
	 */
	public Launch aSynchLaunch() throws LaunchException {
		JobDataMap jobDataMap = new JobDataMap();
		jobDataMap.put(Launch.PARAMETERS, this.launch.getInputParameters());
		jobDataMap.put(Launch.DEFAULT_EXECUTOR_KEY, this.launch.getExecutorService());
		try {
			this.launch.aSynchLaunch();
		} catch (org.parallelj.launching.LaunchException e) {
			throw new LaunchException(e);
		}
		//org.parallelj.launching.LaunchResult result = this.launch.getLaunchResult();
		//jobDataMap.put(QuartzUtils.RETURN_CODE, result.getStatusCode());
		//jobDataMap.put(QuartzUtils.USER_RETURN_CODE, result.getReturnCode());
		//jobDataMap.put(QuartzUtils.PROCEDURES_IN_ERROR, result.getProceduresInError());
		//jobDataMap.put(Launch.OUTPUTS, result.getOutputParameters());
		this.legacyLaunchResult = new LaunchResult(launch.getLaunchId(), jobDataMap);
		
		return this;
	}

	/**
	 * Add a Quartz JobData to the one used to launch the Program. This JobData
	 * is used to initialize Programs arguments for launching.
	 * 
	 * @param jobDataMap
	 *            A JobDatamap
	 * @return This Launch instance.
	 */
	public synchronized Launch addDatas(final JobDataMap jobDataMap) {
		Map<String,Object> data = new HashMap<>();
		for (String key:jobDataMap.getKeys()) {
			data.put(key, jobDataMap.get(key));
		}
		this.launch.addDatas(data);
		return this;
	}

	/**
	 * Get the JobId generated by Quartz when launching the Program.
	 * 
	 * @return the JobId.
	 */
	public String getLaunchId() {
		return this.launch.getLaunchId();
	}

	/**
	 * Get the result Object of the Launch.
	 * 
	 * @return The result Object of the launch.
	 */
	public JobDataMap getLaunchResult() {
		return this.legacyLaunchResult.getResult();
	}

	/**
	 * @return
	 */
	public Job getAdapter() {
		return (Job)this.launch.getJobInstance();
	}

	public void addParameter(String name, Object value) {
		Map<String, Object> dataMap = new HashMap<>();
		dataMap.put(name, value);
		this.launch.addDatas(dataMap);
	}

	public Map<String, Object> getOuputs() {
		return this.launch.getLaunchResult().getOutputParameters();
	}
	
}
