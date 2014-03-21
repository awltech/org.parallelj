package org.parallelj.launching.executors;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;

import org.parallelj.internal.conf.pojos.CExecutor;
import org.parallelj.internal.conf.pojos.ExecutorType;
import org.parallelj.internal.kernel.KProcess;

public class InstanceExecutorService extends AbstractExecutorServiceType {

	private Map<KProcess, ExecutorService> executors = new ConcurrentHashMap<>();
	private Map<String, CExecutor> nonUniqueExecutors = new HashMap<>();

	@Override
	public ExecutorService get(KProcess process) {
		// Is it available in the executor list ? yes=> return it...
		ExecutorService service = this.executors.get(process);
		if (service == null) {
			// Check if the corresponding process is defined as instance in configuration file
			// yes => instanciate it...
			CExecutor cExec = this.nonUniqueExecutors.get(process.getContext().getClass().getCanonicalName());
			if (cExec!=null) {
				service = instanciateExecutor(cExec);
				this.executors.put(process, service);
			}
			if(service == null && process.getParentProcess()!=null) {
				service = get(process.getParentProcess());
			}
		}
		return service;
	}

	@Override
	public void complete(KProcess process) {
		ExecutorService service = this.executors.remove(process);
		if(service != null && !service.isShutdown()) {
			service.shutdown();
		}
	}

	@Override
	public void clean() {
		for (KProcess knownProcess : this.executors.keySet()) {
			complete(knownProcess);
		}
		this.nonUniqueExecutors.clear();
	}

	@Override
	public void add(CExecutor cExecutor) {
		if (cExecutor.getServiceType()!= null && cExecutor.getType()==ExecutorType.INSTANCE) {
			for (String name : cExecutor.getProgramName().split(";")) {
				this.nonUniqueExecutors.put(name, cExecutor);
			}
		}
	}

	@Override
	public int getCount() {
		return this.executors.size();
	}
}
