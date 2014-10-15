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

import org.parallelj.Programs;
import org.parallelj.internal.conf.ConfigurationService;
import org.parallelj.internal.conf.ParalleljConfigurationManager;
import org.parallelj.internal.conf.pojos.CExecutors;
import org.parallelj.internal.conf.pojos.ParalleljConfiguration;
import org.parallelj.internal.kernel.KProcess;
import org.parallelj.internal.kernel.KProcessor;
import org.parallelj.internal.kernel.procedure.CallableProcedure.CallableCall.CallableCallRunnable;
import org.parallelj.internal.kernel.procedure.RunnableProcedure.RunnableCall.RunnableCallRunnable;
import org.parallelj.internal.kernel.procedure.SubProcessProcedure.SubProcessCall.SubProcessRunnable;
import org.parallelj.internal.reflect.ProcessHelperImpl;
import org.parallelj.launching.Launch;
import org.parallelj.launching.LaunchException;
import org.parallelj.launching.LaunchingMessageKind;
import org.parallelj.launching.ProgramReturnCodes;
import org.parallelj.launching.internal.LaunchManagement.LaunchAdapter.IKProcessorLaunch;
import org.parallelj.mirror.ParallelJThreadFactory;

import org.parallelj.launching.executors.ExecutorServiceManager;

/**
 * Implements the synchLaunch(..) and aSynchLaunch(..) methods of Launch.
 * 
 * 
 */
privileged aspect LaunchManagement { 
	
	private static Map<KProcessor, LaunchImpl<?>> phl = new ConcurrentHashMap<>(); 
	
	static privileged public aspect LaunchAdapter percflow(launchInstance() ) {
	
		declare precedence :
			org.parallelj.internal.kernel.Identifiers,
			org.parallelj.internal.reflect.ProgramAdapter,
			org.parallelj.internal.util.sm.impl.KStateMachines,
			org.parallelj.internal.util.sm.impl.KStateMachines.PerMachine,
			org.parallelj.Executables$PerExecutable,
			org.parallelj.internal.reflect.ProgramAdapter.PerProgram,
			org.parallelj.internal.log.Logs;
	
		pointcut launchInstance() : execution(* Launch+.*(..));
	
		interface IKProcessorLaunch {}
		LaunchImpl<?> IKProcessorLaunch.launch;
		declare parents: KProcessor implements IKProcessorLaunch;
		
		interface ILaunchExecutors {}
		ExecutorServiceManager ILaunchExecutors.executorServiceManager;		
		declare parents: LaunchImpl implements ILaunchExecutors;
		
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
			launch.getLaunchResult().setStatusCode(ProgramReturnCodes.RUNNING);
			this.observable.prepareLaunching(launch);
	
			return internalSyncLaunch(launch);
		}
	
		@SuppressWarnings({ "rawtypes", "unchecked" })
		Launch around(LaunchImpl launch) throws LaunchException: 
					execution(public Launch Launch+.aSynchLaunch() throws LaunchException)
						&& this(launch) {
			proceed(launch);
			launch.processHelper = Programs.as(launch.jobInstance);
			launch.getLaunchResult().setStatusCode(ProgramReturnCodes.RUNNING);
			this.observable.prepareLaunching(launch);
			if (launch.getExecutorService()!=null) {
				launch.getExecutorService().execute(new AsyncThread(launch, this));
			} else {
				ParallelJThreadFactory.getInstance().newThread(new AsyncThread(launch, this)).start();
	 		}
			return launch;
		}
	
		@SuppressWarnings("rawtypes")
		private Launch<?> internalSyncLaunch(LaunchImpl<?> launch) {
			LaunchingMessageKind.ILAUNCH0002.format(launch.getJobInstance()
					.getClass().getCanonicalName(), launch.getLaunchId());
			initializeExecutors(launch);
			ProcessHelperImpl processHelper = (ProcessHelperImpl)launch.getProcessHelper();
			KProcessor processor = processHelper.processor;
			if (processor == null) {
				if ( ((ILaunchExecutors)launch).executorServiceManager!=null 
						&& ((ILaunchExecutors)launch).executorServiceManager.getDefaultExecutor()!=null) {
					processor = new KProcessor(((ILaunchExecutors)launch).executorServiceManager.getDefaultExecutor());
				} else {
					processor = new KProcessor(launch.getExecutorService());
				}
				((IKProcessorLaunch)processor).launch = launch;
				phl.put(processor, launch);
			}
			processor.execute(processHelper.process);
			launch.getProcessHelper().join();
			this.observable.finalizeLaunching(launch);
			launch.complete();
			if(launch.executorServiceManager!=null) {
				launch.executorServiceManager.clean();
			}
			phl.remove(processor);
			
			if (launch.getLaunchResult().getStatusCode()!=ProgramReturnCodes.FAILURE
					&&launch.getLaunchResult().getStatusCode()!=ProgramReturnCodes.ABORTED) {
				launch.getLaunchResult().setStatusCode(ProgramReturnCodes.SUCCESS);
			}
			LaunchingMessageKind.ILAUNCH0003.format(launch.getJobInstance()
					.getClass().getCanonicalName(), launch.getLaunchId(), launch
					.getLaunchResult().getStatusCode(), launch.getLaunchResult()
					.getReturnCode());
			return launch;
		}
		
		private void initializeExecutors(LaunchImpl<?> launch) {
			ParalleljConfiguration configuration = (ParalleljConfiguration) ConfigurationService
					.getConfigurationService().getConfigurationManager()
					.get(ParalleljConfigurationManager.class)
					.getConfiguration();
			CExecutors cExecutors = configuration != null ? configuration
					.getExecutorServices() : null;
			if (cExecutors != null) {
				launch.executorServiceManager = new ExecutorServiceManager(cExecutors);
			}
		}
	}

    void around(KProcessor kProcessor, SubProcessRunnable runnable):execution(public void submit(..))
                && args(runnable)
                &&this(kProcessor){
		LaunchImpl<?> launch = phl.get(kProcessor);
		KProcess process = runnable.getSubProcessCall().getProcess();
		if (launch!=null && launch.executorServiceManager!=null) {
			ExecutorService service = launch.executorServiceManager.get(process);
			if (service!=null) {
				kProcessor.submit(runnable, service);
			} else {
				proceed(kProcessor, runnable);
			}
		} else {
			proceed(kProcessor, runnable);
		}
    }

    void around(KProcessor kProcessor, CallableCallRunnable runnable):execution(public void submit(..))
            && args(runnable)
            &&this(kProcessor){
		LaunchImpl<?> launch = phl.get(kProcessor);
		KProcess process = runnable.getCallableCall().getProcess();
		if (launch!=null && launch.executorServiceManager!=null) {
			ExecutorService service = launch.executorServiceManager.get(process);
			if (service!=null) {
				kProcessor.submit(runnable, service);
			} else {
				proceed(kProcessor, runnable);
			}
		} else {
			proceed(kProcessor, runnable);
		}
    }

    void around(KProcessor kProcessor, RunnableCallRunnable runnable):execution(public void submit(..))
            && args(runnable)
            &&this(kProcessor) {
		LaunchImpl<?> launch = phl.get(kProcessor);
		KProcess process = runnable.getRunnableCall().getProcess();
		if (launch!=null && launch.executorServiceManager!=null) {
			ExecutorService service = launch.executorServiceManager.get(process);
			if (service!=null) {
				kProcessor.submit(runnable, service);
			} else {
				proceed(kProcessor, runnable);
			}
		} else {
			proceed(kProcessor, runnable);
		}
    }

    void around(KProcess kProcess):execution(private void complete(..))
    	&& this(kProcess) {
    	proceed(kProcess);
		LaunchImpl<?> launch = ((IKProcessorLaunch)kProcess.getProcessor()).launch;
		if(launch != null && launch.executorServiceManager!=null) {
			launch.executorServiceManager.complete(kProcess);
		}
    }

 }
