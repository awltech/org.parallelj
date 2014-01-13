package org.parallelj.launching.internal;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;

import org.parallelj.internal.conf.pojos.CExecutor;
import org.parallelj.internal.conf.pojos.CExecutors;
import org.parallelj.internal.conf.pojos.ExecutorType;
import org.parallelj.internal.kernel.KProcess;
import org.parallelj.launching.LaunchingMessageKind;
import org.parallelj.mirror.ExecutorServiceKind;

public class ExecutorManager {
	
	Map<String, CExecutor> nonUniqueCExecutors = new HashMap<>();
	
	private ExecutorService defaultExecutor = null;
	private CExecutors cExecutors = null;
	
	private Map<Object, ExecutorService> executors = new ConcurrentHashMap<>();
	
	public ExecutorManager(CExecutors cExecutors) {
		this.cExecutors = cExecutors;
		if(this.cExecutors!=null) {
			initializeDefault();
			initializeOthers();
		}
	}

	private void initializeDefault() {
		if (this.cExecutors.getDefaultServiceType() != null
				|| this.cExecutors.getDefaultPoolSize() != null
				|| this.cExecutors.getDefaultServiceClass() != null) {
			try {
				int size = this.cExecutors.getDefaultPoolSize()!=null?this.cExecutors.getDefaultPoolSize().intValue():1;
				ExecutorService defaultService = ExecutorServiceKind
						.valueOf(
								this.cExecutors.getDefaultServiceType()
										.value()).create(
												size,
										this.cExecutors.getDefaultServiceClass());
				this.defaultExecutor = defaultService;
			} catch (InstantiationException | IllegalAccessException
					| ClassNotFoundException e) {
				// Do nothing..
			} catch (Exception e) {
				LaunchingMessageKind.ELAUNCH0011.format(e);
			}
		}
	}
	
	private void initializeOthers() {
		if (this.cExecutors.getExecutorService() != null) {
			for (CExecutor cExecutor : this.cExecutors.getExecutorService()) {
				String[] names = cExecutor.getProgramName().split(",");
				boolean instanciate = false;
				for (String name : names) {
					if (name.length() > 0 && name.trim().length() > 0) {
						instanciate = true;
					}
				}
				if (instanciate) {
					if (cExecutor.getServiceType()!= null) { 
						if(cExecutor.getType() == ExecutorType.CLASS) {
							instanciateExecutor(cExecutor, false, names);
						} else {
							this.nonUniqueCExecutors.put(cExecutor.getProgramName(), cExecutor);
						}
					}
				}
			}
		}
	}

	public ExecutorService getDefaultExecutor() {
		return this.defaultExecutor;
	}
	
	public void cleanExecutors() {
		for (Object key : this.executors.keySet()) {
			this.executors.get(key).shutdown();
			this.executors.remove(key);
		}
		if (this.defaultExecutor!=null) {
			this.defaultExecutor.shutdown();
		}
		this.nonUniqueCExecutors.clear();
	}
	
	
	private ExecutorService instanciateExecutor(CExecutor cExecutor, boolean nonUnique, String... names) {
		ExecutorService executor=null;
		String type = cExecutor.getServiceType()
				.value();
		try {
			int size = cExecutor.getPoolSize() != null ? cExecutor
					.getPoolSize().intValue() : 0;
			executor = ExecutorServiceKind
					.valueOf(type)
					.create(size,
							cExecutor.getServiceClass());
			if(!nonUnique) {
				boolean foundProgram = false;
				for (String name : names) {
					if (name.length() > 0 && name.trim().length() > 0) {
						this.executors.put(
								name,
								executor);
						foundProgram = true;
					}
				}
				if (!foundProgram) {
					executor.shutdown();
				}
			}
		} catch (InstantiationException
				| IllegalAccessException
				| ClassNotFoundException e) {
			// Do Nothing..
		} catch (Exception e) {
			LaunchingMessageKind.ELAUNCH0012.format(e);
		}
		return executor;
		
	}
	
	public ExecutorService get(KProcess process) {
		// Get executor for the class if configured
		ExecutorService service = this.executors.get(process.getClass().getCanonicalName());
		if(service==null) {
			// get executor from the instance
			service = getFromInstance(process);
		}
		return service;
	}
	
	private ExecutorService getFromInstance(KProcess process) {
		ExecutorService service = this.executors.get(process.getContext().getClass().getCanonicalName());
		if(service == null) {
			service = this.executors.get(process);
			if(service == null) {
				// Check if the corresponding process is defined as instance in configuration file
				CExecutor exec = nonUniqueCExecutors.get(process.getContext().getClass().getCanonicalName());
				if (exec!=null) {
					service = instanciateExecutor(exec, true, new String[]{});
					this.executors.put(process, service);
				}
			}
			if(service == null && process.getParentProcess()!=null) {
				service=getFromInstance(process.getParentProcess());
			}
		}
		return service;
	}
}
