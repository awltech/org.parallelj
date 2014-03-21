package org.parallelj.launching.executors;

import java.util.concurrent.ExecutorService;

import org.parallelj.internal.conf.pojos.CExecutor;
import org.parallelj.internal.kernel.KProcess;
import org.parallelj.launching.LaunchingMessageKind;
import org.parallelj.mirror.ExecutorServiceKind;

public abstract class AbstractExecutorServiceType implements IExecutorServiceType{

	@Override
	public void complete(KProcess process) {
	}
	
	@Override
	public void add(CExecutor cExecutor) {
	}

	protected ExecutorService instanciateExecutor(CExecutor cExec) {
		ExecutorService executor=null;
		String type = cExec.getServiceType()
				.value();
		try {
			int size = cExec.getPoolSize() != null ? cExec
					.getPoolSize().intValue() : 0;
			executor = ExecutorServiceKind
					.valueOf(type)
					.create(size,
							cExec.getServiceClass());
		} catch (InstantiationException
				| IllegalAccessException
				| ClassNotFoundException e) {
			// Do Nothing..
		} catch (Exception e) {
			LaunchingMessageKind.ELAUNCH0012.format(e);
		}
		return executor;
	}
	
}
