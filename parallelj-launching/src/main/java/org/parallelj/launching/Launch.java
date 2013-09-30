package org.parallelj.launching;

import java.util.Map;
import java.util.concurrent.ExecutorService;

import org.parallelj.Programs.ProcessHelper;

public interface Launch<T> {

	public void addParameter(String name, String value);
	public Launch<T> synchLaunch() throws LaunchException;
	public Launch<T> aSynchLaunch() throws LaunchException;
	public LaunchResult getLaunchResult();
	public Map<String, Object> getInputParameters();
	public void setInputParameters(Map<String, Object> inputParameters);
	public ProcessHelper<?> getProcessHelper();
	public T getJobInstance();
	public Launch<T> addAllData(Map<String, Object> dataMap);
	public String getLaunchId();
	public ExecutorService getExecutorService();
}
