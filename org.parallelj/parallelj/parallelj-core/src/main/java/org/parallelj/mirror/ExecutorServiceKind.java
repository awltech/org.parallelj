package org.parallelj.mirror;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * The kind of {@link ExecutorService} that can be used by the {@link Processor}
 * 
 * @author Laurent Legrand
 * 
 */
public enum ExecutorServiceKind {

	/**
	 * Use the current thread.
	 */
	NONE,

	/**
	 * Use a {@link Executors#newSingleThreadExecutor()}.
	 */
	SINGLE_THREAD_EXECUTOR,

	/**
	 * Use a {@link Executors#newCachedThreadPool()}.
	 */
	CACHED_THREAD_POOL,
	
	/**
	 * Use a {@link Executors#newFixedThreadPool(int)}.
	 */
	FIXED_THREAD_POOL,
	
	/**
	 * Use a provided executor service
	 */
	PROVIDED;

}
