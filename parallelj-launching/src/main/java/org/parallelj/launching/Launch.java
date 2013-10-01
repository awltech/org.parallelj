package org.parallelj.launching;

import java.util.Map;
import java.util.concurrent.ExecutorService;

import org.parallelj.Programs.ProcessHelper;

public interface Launch<T> {

	public void addParameter(String name, String value);
	public Launch<T> addParameters(Map<String, Object> dataMap);
	public Launch<T> synchLaunch() throws LaunchException;
	public Launch<T> aSynchLaunch() throws LaunchException;
	public LaunchResult getLaunchResult();
	public Map<String, Object> getParameters();
	public ProcessHelper<?> getProcessHelper();
	public T getJobInstance();
	public String getLaunchId();
	public ExecutorService getExecutorService();
}
