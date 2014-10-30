/*
 *     ParallelJ, framework for parallel computing
 *
 *     Copyright (C) 2010, 2011, 2012 Atos Worldline or third-party contributors as
 *     indicated by the @author tags or express copyright attribution
 *     statements applied by the authors.
 *
 *     This library is free software; you can redistribute it and/or
 *     modify it under the terms of the GNU Lesser General Public
 *     License as published by the Free Software Foundation; either
 *     version 2.1 of the License.
 *
 *     This library is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 *     Lesser General Public License for more details.
 *
 *     You should have received a copy of the GNU Lesser General Public
 *     License along with this library; if not, write to the Free Software
 *     Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA
 */
package org.parallelj.launching.executors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.parallelj.internal.conf.pojos.CExecutor;
import org.parallelj.internal.conf.pojos.ExecutorType;
import org.parallelj.internal.kernel.KProcess;

public class InstanceExecutorService extends AbstractExecutorServiceType {

	private Map<KProcess, ExecutorService> executors = new ConcurrentHashMap<>();
	private Map<String, CExecutor> cExecutors = new HashMap<>();
	
	private List<ExecutorService> instanceExecutorServiceList = new ArrayList<>();   

	private Lock lock = new ReentrantLock();

	@Override
	public ExecutorService get(KProcess process) {
		// Is it available in the executor list ? yes=> return it...
		ExecutorService service = this.executors.get(process);
		if (service == null) {
			// Check if the corresponding process is defined as instance in configuration file
			// yes => instanciate it...
			CExecutor cExec = this.cExecutors.get(process.getContext().getClass().getCanonicalName());
			if (cExec!=null) {
				// look in instanceExecutorServiceList (cache) if one is available
				try {
					lock.lock();
					if (this.instanceExecutorServiceList.size()>0) {
						service = this.instanceExecutorServiceList.remove(0);
						this.executors.put(process, service);
					} else {
						service = instanciateExecutor(cExec);
						this.executors.put(process, service);
					}
				} finally {
					lock.unlock();
				}
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
		// Put it in instanceExecutorServiceList (cache)
		if(service != null && !service.isShutdown()) {
			try {
				lock.lock();
				this.instanceExecutorServiceList.add(service);
			} finally {
				lock.unlock();
			}
		}
	}

	@Override
	public void clean() {
		for (KProcess knownProcess : this.executors.keySet()) {
			ExecutorService service = this.executors.remove(knownProcess);
			if(service != null && !service.isShutdown()) {
				service.shutdown();
			}
		}
		try {
			lock.lock();
			for (ExecutorService service: this.instanceExecutorServiceList) {
				if(service != null && !service.isShutdown()) {
					service.shutdown();
				}
			}
		} finally {
			lock.unlock();
		}
		this.cExecutors.clear();
	}

	@Override
	public void add(CExecutor cExecutor) {
		if (cExecutor.getServiceType()!= null && cExecutor.getType()==ExecutorType.INSTANCE) {
			for (String name : cExecutor.getProgramName().split(";")) {
				this.cExecutors.put(name, cExecutor);
			}
		}
	}

	@Override
	public int getCount() {
		return this.executors.size();
	}
}
