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
package org.parallelj.launching.internal;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;

import org.parallelj.internal.kernel.KProcessor;
import org.parallelj.launching.Launch;
import org.parallelj.launching.LaunchException;
import org.parallelj.launching.LaunchingMessageKind;
import org.parallelj.mirror.ProcessState;

/**
 * Implements the Job.execute(..) method
 * 
 * 
 */
privileged public aspect LaunchAdapter {

	/*
	 * The Aspect JobsAdapter must be passed before this.
	 */
	declare precedence :
		org.parallelj.internal.kernel.Identifiers,
		org.parallelj.internal.reflect.ProgramAdapter,
		org.parallelj.internal.util.sm.impl.KStateMachines,
		org.parallelj.internal.util.sm.impl.KStateMachines.PerMachine,
		org.parallelj.Executables$PerExecutable,
		org.parallelj.internal.reflect.ProgramAdapter.PerProgram,
		org.parallelj.internal.log.Logs;


	private LaunchingObservable observable = new LaunchingObservable();
	
	private Map<KProcessor, Launch> launchProcessors= new ConcurrentHashMap<>();

	/**
	 * Launch a Program and initialize the Result as a JobDataMap.
	 * 
	 * @param self
	 * @param context
	 * @throws JobExecutionException
	 */
	void around(Launch self) : 
	execution( private void  initializeInstance(..) ) && this(self) {

		proceed(self);

		this.observable.prepareLaunching(self);
	}
	
	
	void around(Launch self, Object programInstance, ExecutorService executorService) throws LaunchException: 
	execution( private void internalaSynchLaunch(..) throws LaunchException)
	&& args(programInstance, executorService)
	&& this(self) {
		if (self.getProcessHelper().getProcess().getState() != ProcessState.PENDING) {
			throw new LaunchException(LaunchingMessageKind.ELAUNCH0009.getFormatedMessage(programInstance));
		}
		
		proceed(self, programInstance, executorService);

		this.launchProcessors.put(((KProcessor)self.getProcessHelper().getProcess().getProcessor()), self);
	}

	after(org.parallelj.internal.kernel.KProcessor self):
		execution(@org.parallelj.internal.util.sm.Trigger void complete()) 
		&& this(self) {
		
		Launch launch = this.launchProcessors.get(self);
		if (launch!=null) {
			this.observable.finalizeLaunching(launch);
			launch.finalizeInstance();
			this.launchProcessors.remove(self);
		}
	}

}
