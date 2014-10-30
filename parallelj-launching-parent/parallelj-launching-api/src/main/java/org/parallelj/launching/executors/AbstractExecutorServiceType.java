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
