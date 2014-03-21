package org.parallelj.launching.executors;

import java.util.concurrent.ExecutorService;

import org.parallelj.internal.conf.pojos.CExecutor;
import org.parallelj.internal.kernel.KProcess;

public interface IExecutorServiceType {
	
	public ExecutorService get(KProcess process);
	
	public void complete(KProcess process);
	
	public void clean();
	
	public void add(CExecutor cExecutor);
	
	public int getCount();
}
