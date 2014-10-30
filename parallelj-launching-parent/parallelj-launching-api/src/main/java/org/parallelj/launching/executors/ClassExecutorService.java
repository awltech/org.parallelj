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
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;

import org.parallelj.internal.conf.pojos.CExecutor;
import org.parallelj.internal.conf.pojos.ExecutorType;
import org.parallelj.internal.kernel.KProcess;
import org.parallelj.launching.LaunchingMessageKind;
import org.parallelj.mirror.ExecutorServiceKind;

public class ClassExecutorService extends AbstractExecutorServiceType {
	
	private Map<Class<?>, ExecutorService> executors = new ConcurrentHashMap<>();

	@Override
	public ExecutorService get(KProcess process) {
		Class<?> processClass = (Class<?>)process.getContext().getClass(); 
		for (Class<?> knownProcessClass : this.executors.keySet()) {
			if (processClass.equals(knownProcessClass)) {
				return this.executors.get(knownProcessClass);
			}
		}
		return null;
	}

	@Override
	public void clean() {
		Set<Class<?>> knownProcessClasses = this.executors.keySet(); 
		for (Class<?> knownProcessClass : knownProcessClasses) {
			ExecutorService service = this.executors.remove(knownProcessClass);
			if(service != null && !service.isShutdown()) {
				service.shutdown();
			}
		}
	}

	@Override
	public void add(CExecutor cExecutor) {
		if (cExecutor.getServiceType()!= null && cExecutor.getType()==ExecutorType.CLASS) {
			ExecutorService executor=null;
			String type = cExecutor.getServiceType()
					.value();
			int size = cExecutor.getPoolSize() != null ? cExecutor
					.getPoolSize().intValue() : 0;
			String names[] = cExecutor.getProgramName().split(",");
			// Initialize a list of class for this configuration part, if defined classes are valid...
			List<Class<?>> classes = new ArrayList<>();
			for (String name : names) {
				try {
					Class<?> currentClass = Class.forName(name, true, ClassExecutorService.class.getClassLoader());
					classes.add(currentClass);
				} catch (ClassNotFoundException e) {
					// Do Nothing..
					LaunchingMessageKind.ELAUNCH0012.format(e);
				}
			}
			
			// If there is at least one valid class, create an Executor service and assign it to the class
			if(classes.size()>0) {
				try {
					executor = ExecutorServiceKind
							.valueOf(type)
							.create(size,
									cExecutor.getServiceClass());
					for (Class<?> currentClass : classes) {
						this.executors.put(currentClass,executor);
					}
				} catch (InstantiationException
						| IllegalAccessException
						| ClassNotFoundException e) {
					// Do Nothing..
					LaunchingMessageKind.ELAUNCH0012.format(e);
				}
			}
		}
	}

	@Override
	public int getCount() {
		return this.executors.size();
	}

}
