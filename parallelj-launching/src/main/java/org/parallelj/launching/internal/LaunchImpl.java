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
import java.util.concurrent.Executors;

import org.parallelj.Programs;
import org.parallelj.Programs.ProcessHelper;
import org.parallelj.internal.kernel.KProgram;
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
	boolean multiThreading=true;

	private Map<String, Object> inputParameters = new HashMap<String, Object>();

	/**
	 * The Executor service to use when launching the program associated to this
	 * Launch
	 */
	private ExecutorService executorService = null;
	private boolean stopExecutorServiceAfterExecution = false;

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

		checkProgramInstance();
	}

	@Override
	public void addParameter(String name, String value) {
		this.inputParameters.put(name, value);
	}

	@SuppressWarnings("unchecked")
	private void checkProgramInstance() throws LaunchException {
		if (this.jobClass == null && this.jobInstance == null) {
			throw new LaunchException("");
		} else if (this.jobClass != null && this.jobInstance == null) {
			try {
				this.jobInstance = (T) this.jobClass.newInstance();
			} catch (InstantiationException e) {
				throw new LaunchException(e);
			} catch (IllegalAccessException e) {
				throw new LaunchException(e);
			}
		} else if (this.jobClass == null && this.jobInstance != null) {
			this.jobClass = this.jobInstance.getClass();
		}
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

		if (this.multiThreading && this.executorService == null) {
			short programCapacity = ((KProgram) this.getProcessHelper()
					.getProcess().getProgram()).getCapacity();
			this.executorService = (programCapacity == Short.MAX_VALUE) ? Executors
					.newFixedThreadPool(100, new ParallelJThreadFactory())
					: Executors.newFixedThreadPool(programCapacity,
							new ParallelJThreadFactory());
			this.stopExecutorServiceAfterExecution=true;
		}

		if (this.launchId == null) {
			this.launchId = UUID.randomUUID();
		}
		this.launchResult = new LaunchResult();
	}

	@SuppressWarnings("unused")
	private void complete() {
		if (this.stopExecutorServiceAfterExecution) {
			this.executorService.shutdown();
		}
	}

	@Override
	public LaunchResult getLaunchResult() {
		return launchResult;
	}

	@Override
	public Map<String, Object> getInputParameters() {
		return inputParameters;
	}

	@Override
	public void setInputParameters(Map<String, Object> inputParameters) {
		this.inputParameters = inputParameters;
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
	public Launch<T> addAllData(Map<String, Object> dataMap) {
		this.inputParameters.putAll(dataMap);
		return this;
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

	public void setMultiThreading(boolean multiThreading) {
		this.multiThreading = multiThreading;
	}

}
