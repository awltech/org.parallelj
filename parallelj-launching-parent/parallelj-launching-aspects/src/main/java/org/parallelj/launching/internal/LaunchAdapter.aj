package org.parallelj.launching.internal;

import org.parallelj.Programs;
import org.parallelj.internal.conf.ConfigurationService;
import org.parallelj.internal.conf.ParalleljConfigurationManager;
import org.parallelj.internal.conf.pojos.CExecutors;
import org.parallelj.internal.conf.pojos.ParalleljConfiguration;
import org.parallelj.internal.kernel.KProcessor;
import org.parallelj.internal.reflect.ProcessHelperImpl;
import org.parallelj.launching.Launch;
import org.parallelj.launching.LaunchException;
import org.parallelj.launching.LaunchingMessageKind;
import org.parallelj.launching.ProgramReturnCodes;
import org.parallelj.launching.executors.ExecutorServiceManager;
import org.parallelj.mirror.ParallelJThreadFactory;

public privileged aspect LaunchAdapter percflow(launchInstance()) {

	declare precedence :
		org.parallelj.internal.kernel.Identifiers,
		org.parallelj.internal.reflect.ProgramAdapter,
		org.parallelj.internal.util.sm.impl.KStateMachines,
		org.parallelj.internal.util.sm.impl.KStateMachines.PerMachine,
		org.parallelj.Executables$PerExecutable,
		org.parallelj.internal.reflect.ProgramAdapter.PerProgram,
		org.parallelj.internal.log.Logs;

	pointcut launchInstance() : execution(* Launch+.*(..));

	declare parents: KProcessor implements IKProcessorLaunch;
	@SuppressWarnings("rawtypes")
	LaunchImpl IKProcessorLaunch.launch;
	
	declare parents: LaunchImpl implements ILaunchExecutors;
	ExecutorServiceManager ILaunchExecutors.executorServiceManager;
	
	public ExecutorServiceManager ILaunchExecutors.getExecutorServiceManager() {
		return this.executorServiceManager;
	}
	
	public void ILaunchExecutors.setExecutorServiceManager(ExecutorServiceManager executorServiceManager) {
		this.executorServiceManager = executorServiceManager;
	}
	
	private LaunchingObservable observable = new LaunchingObservable();
	
	boolean stopExecutorServiceAfterExecution = false;

	private static class AsyncThread implements Runnable {

		private LaunchAdapter launchAdapter;
		@SuppressWarnings("rawtypes")
		private LaunchImpl launch;

		@SuppressWarnings("rawtypes")
		public AsyncThread( LaunchImpl launch, LaunchAdapter launchAdapter) {
			this.launch = launch;
			this.launchAdapter = launchAdapter;
		}

		@Override
		public void run() {
			this.launchAdapter.internalSyncLaunch(this.launch);
		}
	}
	
	@SuppressWarnings("rawtypes")
	Launch around(LaunchImpl launch) throws LaunchException: 
				execution(public Launch Launch+.synchLaunch(..) throws LaunchException)
					&& this(launch) {
		proceed(launch);
		launch.processHelper = Programs.as(launch.jobInstance);
		launch.getLaunchResult().setStatusCode(ProgramReturnCodes.RUNNING);
		this.observable.prepareLaunching(launch);

		return internalSyncLaunch(launch);
	}

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
	private Launch internalSyncLaunch(LaunchImpl launch) {
		LaunchingMessageKind.ILAUNCH0002.format(launch.getJobInstance()
				.getClass().getCanonicalName(), launch.getLaunchId());
		initializeExecutors(launch);
		ProcessHelperImpl processHelper = (ProcessHelperImpl)launch.getProcessHelper();
		KProcessor processor = processHelper.processor;
		if (processor == null) {
			if ( ((ILaunchExecutors)launch).getExecutorServiceManager()!=null 
					&& ((ILaunchExecutors)launch).getExecutorServiceManager().getDefaultExecutor()!=null) {
				processor = new KProcessor(((ILaunchExecutors)launch).getExecutorServiceManager().getDefaultExecutor());
			} else {
				processor = new KProcessor(launch.getExecutorService());
			}
			((IKProcessorLaunch)processor).launch = launch;
			LaunchManagement.phl.put(processor, launch);
		}
		processor.execute(processHelper.process);
		launch.getProcessHelper().join();
		this.observable.finalizeLaunching(launch);
		launch.complete();
		if(((ILaunchExecutors)launch).getExecutorServiceManager()!=null) {
			((ILaunchExecutors)launch).getExecutorServiceManager().clean();
		}
		LaunchManagement.phl.remove(processor);
		
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
	
	private void initializeExecutors(LaunchImpl launch) {
		ParalleljConfiguration configuration = (ParalleljConfiguration) ConfigurationService
				.getConfigurationService().getConfigurationManager()
				.get(ParalleljConfigurationManager.class)
				.getConfiguration();
		CExecutors cExecutors = configuration != null ? configuration
				.getExecutorServices() : null;
		if (cExecutors != null) {
			((ILaunchExecutors)launch).setExecutorServiceManager(new ExecutorServiceManager(cExecutors));
		}
	}
}
