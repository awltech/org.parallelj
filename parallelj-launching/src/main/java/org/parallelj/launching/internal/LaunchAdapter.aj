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

import org.parallelj.Programs;
import org.parallelj.launching.Launch;
import org.parallelj.launching.LaunchException;
import org.parallelj.launching.LaunchingMessageKind;

/**
 * Implements the synchLaunch(..) and aSynchLaunch(..) methods of Launch.
 * 
 * 
 */
privileged public aspect LaunchAdapter perthis(launchInstance() ) {

	declare precedence :
		org.parallelj.internal.kernel.Identifiers,
		org.parallelj.internal.reflect.ProgramAdapter,
		org.parallelj.internal.util.sm.impl.KStateMachines,
		org.parallelj.internal.util.sm.impl.KStateMachines.PerMachine,
		org.parallelj.Executables$PerExecutable,
		org.parallelj.internal.reflect.ProgramAdapter.PerProgram,
		org.parallelj.internal.log.Logs;

	pointcut launchInstance() : execution(* Launch+.*(..));

	private LaunchingObservable observable = new LaunchingObservable();

	boolean stopExecutorServiceAfterExecution = false;

	private static class AsyncThread implements Runnable {

		private LaunchAdapter launchAdapter;
		private LaunchImpl<?> launch;

		public AsyncThread(LaunchImpl<?> launch, LaunchAdapter launchAdapter) {
			this.launch = launch;
			this.launchAdapter = launchAdapter;
		}

		@Override
		public void run() {
			this.launchAdapter.internalSyncLaunch(this.launch);
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	Launch around(LaunchImpl launch) throws LaunchException: 
				execution(public Launch Launch+.synchLaunch(..) throws LaunchException)
					&& this(launch) {
		proceed(launch);
		launch.processHelper = Programs.as(launch.jobInstance);
		this.observable.prepareLaunching(launch);

		return internalSyncLaunch(launch);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	Launch around(LaunchImpl launch) throws LaunchException: 
				execution(public Launch Launch+.aSynchLaunch() throws LaunchException)
					&& this(launch) {
		proceed(launch);
		launch.processHelper = Programs.as(launch.jobInstance);
		this.observable.prepareLaunching(launch);
		launch.getExecutorService().submit(new AsyncThread(launch, this));
		return launch;
	}

	private Launch<?> internalSyncLaunch(LaunchImpl<?> launch) {
		LaunchingMessageKind.ILAUNCH0002.format(launch.getJobInstance()
				.getClass().getCanonicalName(), launch.getLaunchId());
		if (launch.multiThreading && launch.getExecutorService()!=null) {
			launch.getProcessHelper().execute(launch.getExecutorService());
		} else {
			launch.getProcessHelper().execute();
		}
		launch.getProcessHelper().join();
		LaunchingMessageKind.ILAUNCH0003.format(launch.getJobInstance()
				.getClass().getCanonicalName(), launch.getLaunchId(), launch
				.getLaunchResult().getStatusCode(), launch.getLaunchResult()
				.getReturnCode());
		this.observable.finalizeLaunching(launch);
		launch.complete();

		return launch;
	}

}
