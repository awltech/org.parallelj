package org.parallelj.launching;

import java.util.Map;
import java.util.concurrent.ExecutorService;

import org.parallelj.Programs.ProcessHelper;

/**
 * The Launch entry of a Program. A Launch is specific to an execution of a
 * {@link org.parallelj.Program Program}.
 * 
 * @param <T>
 *            The type of the Program to use with this Launch.
 */
public interface Launch<T> {

	/**
	 * Allow to specify a value for an attribute of the
	 * {@link org.parallelj.Program Program} associated with this Launch. The attribute
	 * must be annotated with {@link In}.
	 * 
	 * @param name
	 *            The Program attribute name.
	 * @param value
	 *            The value for the attribute as a String.
	 * 
	 * @return This Launch instance.
	 */
	public void addParameter(String name, String value);

	/**
	 * Allow to specify values for attributes of the
	 * {@link org.parallelj.Program Program} associated with this Launch. The attributes
	 * must be annotated with {@link In}
	 * 
	 * @param dataMap
	 *            A map with the Program attributes values (key : the attribute
	 *            name; value : the attibute value as a String)
	 * 
	 * @return This Launch instance.
	 */
	public Launch<T> addParameters(Map<String, Object> dataMap);

	/**
	 * Launch a Program and wait until it's terminated. Note that this method
	 * can be called only once for a same {@link org.parallelj.Program Program}. If you
	 * want to launch again the {@link org.parallelj.Program Program} associated with
	 * this Launch, another instance of Launch be created using
	 * {@link Launcher#newLaunch(Class)}
	 * 
	 * @return This Launch instance.
	 * 
	 * @throws LaunchException
	 */
	public Launch<T> synchLaunch() throws LaunchException;

	/**
	 * Launch a Program and continue. Note that this method can be called only
	 * once for a same {@link org.parallelj.Program Program}. If you want to launch
	 * again the {@link org.parallelj.Program Program} associated with this Launch,
	 * another instance of Launch be created using
	 * {@link Launcher#newLaunch(Class)}
	 * 
	 * @return This Launch instance.
	 * 
	 * @throws LaunchException
	 */
	public Launch<T> aSynchLaunch() throws LaunchException;

	/**
	 * Get the {@link LaunchResult} created during the Launch execution.
	 * 
	 * @return This Launch instance.
	 */
	public LaunchResult getLaunchResult();

	/**
	 * Return the {@link org.parallelj.Program Program} attributes annotated with
	 * {@link In} and its values.
	 * 
	 * @return This Launch instance.
	 */
	public Map<String, Object> getParameters();

	/**
	 * Get the {@link org.parallelj.Programs.ProcessHelper ProcessHelper} created internally
	 * by this Launch.
	 * 
	 * @return This Launch instance.
	 */
	public ProcessHelper<?> getProcessHelper();

	/**
	 * Get the {@link org.parallelj.Program Program} instance created by this Launch.
	 * 
	 * @return This Launch instance.
	 */
	public T getJobInstance();

	/**
	 * Get the unique identifier created by this launch execution.
	 * 
	 * @return This Launch instance.
	 */
	public String getLaunchId();

	/***
	 * Get the {@link ExecutorService} passed as an argument to this Launch
	 * creation using {@link Launcher#newLaunch(Class, ExecutorService)}
	 * 
	 * @return This Launch instance.
	 */
	public ExecutorService getExecutorService();
}
