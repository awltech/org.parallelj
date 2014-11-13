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
import org.parallelj.launching.internal.LaunchImpl;
import org.parallelj.launching.internal.LaunchingObservable;
import org.parallelj.mirror.ParallelJThreadFactory;
import org.parallelj.launching.executors.ExecutorServiceManager;

/**
 * Implements the synchLaunch(..) and aSynchLaunch(..) methods of Launch.
 * 
 * 
 */
privileged aspect LaunchManagement { 
	
	private static Map<KProcessor, LaunchImpl> phl = new ConcurrentHashMap<>(); 
	
    void around(KProcessor kProcessor, SubProcessRunnable runnable):execution(public void submit(..))
                && args(runnable)
                &&this(kProcessor){
		LaunchImpl launch = (LaunchImpl)phl.get(kProcessor);
		KProcess process = runnable.getSubProcessCall().getProcess();
		if (launch!=null && ((ILaunchExecutors)launch).executorServiceManager!=null) {
			ExecutorService service = ((ILaunchExecutors)launch).executorServiceManager.get(process);
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
		LaunchImpl launch = phl.get(kProcessor);
		KProcess process = runnable.getCallableCall().getProcess();
		if (launch!=null && ((ILaunchExecutors)launch).executorServiceManager!=null) {
			ExecutorService service = ((ILaunchExecutors)launch).executorServiceManager.get(process);
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
		LaunchImpl launch = phl.get(kProcessor);
		KProcess process = runnable.getRunnableCall().getProcess();
		if (launch!=null && ((ILaunchExecutors)launch).executorServiceManager!=null) {
			ExecutorService service = ((ILaunchExecutors)launch).executorServiceManager.get(process);
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
		LaunchImpl launch = ((IKProcessorLaunch)kProcess.getProcessor()).launch;
		if(launch != null && ((ILaunchExecutors)launch).executorServiceManager!=null) {
			((ILaunchExecutors)launch).executorServiceManager.complete(kProcess);
		}
    }

 }
