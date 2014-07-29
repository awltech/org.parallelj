package org.parallelj.launching.executors;

import java.util.concurrent.ExecutorService;

import org.parallelj.internal.conf.pojos.CExecutor;
import org.parallelj.internal.conf.pojos.CExecutors;
import org.parallelj.internal.kernel.KProcess;
import org.parallelj.launching.LaunchingMessageKind;
import org.parallelj.mirror.ExecutorServiceKind;

public class ExecutorServiceManager {
	
	private ExecutorService defaultExecutor;
	
	private IExecutorServiceType[] servicesType;

	public ExecutorServiceManager(CExecutors cExecutors) {
		this.servicesType = new IExecutorServiceType[]{new ClassExecutorService(), new InstanceExecutorService()};
		this.add(cExecutors);
		for (CExecutor cExecutor : cExecutors.getExecutorService()) {
			for (IExecutorServiceType iExecutorType : this.servicesType) {
				iExecutorType.add(cExecutor);
			}
		}
	}

	public void add(CExecutors cExecutors) {
		if (cExecutors.getDefaultServiceType() != null
				|| cExecutors.getDefaultPoolSize() != null
				|| cExecutors.getDefaultServiceClass() != null) {
			try {
				int size = cExecutors.getDefaultPoolSize()!=null?cExecutors.getDefaultPoolSize().intValue():1;
				ExecutorService defaultService = ExecutorServiceKind
						.valueOf(
								cExecutors.getDefaultServiceType()
										.value()).create(
												size,
										cExecutors.getDefaultServiceClass());
				this.defaultExecutor = defaultService;
			} catch (InstantiationException | IllegalAccessException
					| ClassNotFoundException e) {
				// Do nothing..
			} catch (Exception e) {
				LaunchingMessageKind.ELAUNCH0011.format(e);
			}
		}
	}

	public ExecutorService getDefaultExecutor() {
		return this.defaultExecutor;
	}

	public ExecutorService get(KProcess process) {
		for (IExecutorServiceType iExecutorServiceType : this.servicesType) {
			ExecutorService service = iExecutorServiceType.get(process);
			if(service!=null) {
				return service;
			}
		}
		return null;
	}

	public void complete(KProcess process) {
		for (IExecutorServiceType iExec : this.servicesType) {
			iExec.complete(process);
		}
	}

	public void clean() {
		for (IExecutorServiceType iExec : this.servicesType) {
			iExec.clean();
		}
		if (this.defaultExecutor!=null && !this.defaultExecutor.isShutdown()) {
			this.defaultExecutor.shutdown();
		}
	}

	@Override
	public String toString() {
		StringBuffer buff = new StringBuffer();
		for (IExecutorServiceType iExecutorServiceType : this.servicesType) {
			buff.append(" **** "+iExecutorServiceType.getClass().getSimpleName() + " -> "+iExecutorServiceType.getCount());
		}
		return buff.toString();
	}
	

}
