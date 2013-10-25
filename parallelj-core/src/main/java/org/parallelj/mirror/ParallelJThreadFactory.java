package org.parallelj.mirror;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Factory in charge of naming threads created so that they appear clearly when
 * performing a thread dump.
 * 
 */
public class ParallelJThreadFactory implements ThreadFactory {

	private static class Holder {
		private static final ParallelJThreadFactory INSTANCE = new ParallelJThreadFactory();
	}

	public static ParallelJThreadFactory getInstance() {
		return ParallelJThreadFactory.Holder.INSTANCE;
	}

	private static final String defaultThreadGroupName="//J-";
	private static final String defaultThreadName="//J-Thread-";
	
	/**
	 * The base name to use when creating threads using this factory.
	 */
	private String threadName=defaultThreadName;
	
	/**
	 * The counter for each created thread appended to the base name.
	 */
	private AtomicLong counter = new AtomicLong();
	
	/**
	 * The threadGroup for each new ParallelJ Thread.
	 */
	private ThreadGroup threadGroup;

	private ParallelJThreadFactory() {
		this.threadGroup=new ThreadGroup(defaultThreadGroupName);
		this.threadName=defaultThreadName;
	}

	@Override
	public Thread newThread(Runnable r) {
		return new Thread(this.threadGroup, r, this.threadName+counter.incrementAndGet());
	}

}