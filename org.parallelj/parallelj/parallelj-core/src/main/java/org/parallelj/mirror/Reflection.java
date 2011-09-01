package org.parallelj.mirror;

import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * Entry point for mirror interfaces.
 * 
 * 
 * @author Laurent Legrand
 * 
 */
public interface Reflection {

	/**
	 * Return the list of {@link ProgramType}
	 * 
	 * @return
	 */
	List<ProgramType> getPrograms();

	/**
	 * Return a new {@link Processor}
	 * 
	 * If executorKind is {@link ExecutorServiceKind#NONE},
	 * {@link ExecutorServiceKind#SINGLE_THREAD_EXECUTOR} or
	 * {@link ExecutorServiceKind#CACHED_THREAD_POOL} then args must be
	 * empty.
	 * 
	 * If executorKind is {@link ExecutorServiceKind#FIXED_THREAD_POOL} then
	 * args must be of type int and must be greater than 1.
	 * 
	 * If executorKind is {@link ExecutorServiceKind#PROVIDED} then args
	 * must be an {@link ExecutorService}.
	 * 
	 * @param executorKind
	 *                the kind of executor
	 * @param args
	 * @return
	 * @throws IllegalArgumentException if one of the arguments does not match
	 */
	Processor newProcessor(ExecutorServiceKind executorKind, Object... args) throws IllegalArgumentException;
	
	/**
	 * Add an {@link EventListener}
	 * 
	 * @param listener the listener to add
	 */
	void addEventListener(EventListener listener);
	
	/**
	 * Remove an {@link EventListener}
	 * 
	 * @param listener the listener to remove
	 */
	void removeEventListener(EventListener listener);

}
