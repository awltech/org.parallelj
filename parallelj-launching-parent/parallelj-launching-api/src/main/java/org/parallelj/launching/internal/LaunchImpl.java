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
package org.parallelj.launching.internal;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;

import org.parallelj.Programs;
import org.parallelj.Programs.ProcessHelper;
import org.parallelj.launching.Launch;
import org.parallelj.launching.LaunchException;
import org.parallelj.launching.LaunchResult;

/**
 * A launch entry of a Program.
 * 
 */
public class LaunchImpl<T> implements Launch<T> {

	/**
	 * The Program Adapter class.
	 */
	private Class<?> jobClass;
	private T jobInstance;
	private UUID launchId;
	private int launchRemoteIndex;
	ProcessHelper<?> processHelper = null;

	private Map<String, Object> inputParameters = new HashMap<String, Object>();

	/**
	 * The Executor service to use when launching the program associated to this
	 * Launch
	 */
	private ExecutorService executorService = null;
	private boolean stopExecutorServiceAfterExecution = true;

	/**
	 * The result Object of this Launch.
	 */
	private LaunchResult launchResult = new LaunchResult();

	public LaunchImpl(Class<? extends T> class1) throws LaunchException {
		this(class1, null);
	}

	public LaunchImpl(Class<? extends T> jobClass,
			ExecutorService executorService) throws LaunchException {
		this.jobClass = jobClass;
		this.executorService = executorService;

		if (this.jobClass == null) {
			throw new LaunchException("Program Class can't be null");
		} else {
			try {
				this.jobInstance = (T) this.jobClass.newInstance();
			} catch (InstantiationException e) {
				throw new LaunchException(e);
			} catch (IllegalAccessException e) {
				throw new LaunchException(e);
			}
		} 
		
		if (this.executorService!=null) {
			this.stopExecutorServiceAfterExecution=false;
		}
	}

	@Override
	public void addParameter(String name, String value) {
		this.inputParameters.put(name, value);
	}

	@Override
	public Launch<T> addParameters(Map<String, Object> dataMap) {
		this.inputParameters.putAll(dataMap);
		return this;
	}
	
	@Override
	public Map<String, Object> getParameters() {
		return this.inputParameters;
	}

	/**
	 * Launch a Program and wait until it's terminated.
	 * 
	 * @return A Launch instance.
	 * @throws LaunchException
	 *             When a SchedulerException occurred.
	 */
	@Override
	public Launch<T> synchLaunch() throws LaunchException {
		initializeInstance();
		return this;
	}

	/**
	 * Launch a Program and continue.
	 * 
	 * @return A Launch instance.
	 * @throws LaunchException
	 *             When a SchedulerException occurred.
	 */
	@Override
	public Launch<T> aSynchLaunch() throws LaunchException {
		initializeInstance();
		return this;
	}

	/**
	 * Initialize the jobInstance:
	 * - ExecutorService if null
	 * - launchId if null
	 * 
	 * @param launch
	 */
	private void initializeInstance() {
		this.processHelper = Programs.as(this.jobInstance);

		if (this.launchId == null) {
			this.launchId = UUID.randomUUID();
		}
		this.launchResult = new LaunchResult();
	}

	@SuppressWarnings("unused")
	private void complete() {
		if (this.executorService!=null && this.stopExecutorServiceAfterExecution) {
			this.executorService.shutdown();
		}
	}

	@Override
	public LaunchResult getLaunchResult() {
		return launchResult;
	}

	@Override
	public ProcessHelper<?> getProcessHelper() {
		return processHelper;
	}

	@Override
	public T getJobInstance() {
		return jobInstance;
	}

	@Override
	public String getLaunchId() {
		return String.valueOf(this.launchId);
	}

	public void setLaunchId(String id) {
		this.launchId = UUID.fromString(id);
	}

	public int getLaunchRemoteIndex() {
		return launchRemoteIndex;
	}

	public void setLaunchRemoteIndex(int launchRemoteIndex) {
		this.launchRemoteIndex = launchRemoteIndex;
	}

	@Override
	public ExecutorService getExecutorService() {
		return executorService;
	}

}
