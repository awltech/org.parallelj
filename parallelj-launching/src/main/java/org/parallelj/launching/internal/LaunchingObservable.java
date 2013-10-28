package org.parallelj.launching.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ServiceLoader;

import org.parallelj.Programs.ProcessHelper;
import org.parallelj.internal.reflect.ProgramAdapter.Adapter;
import org.quartz.JobExecutionContext;

public class LaunchingObservable {

	private static class Holder {
		private static final LaunchingObservable INSTANCE = new LaunchingObservable();
	}

	private static class LaunchingListenerComparator implements Comparator<LaunchingListener> {

	    @Override
	    public int compare(LaunchingListener o1, LaunchingListener o2) {
	    	if(o1.getPriority()>o2.getPriority())
	    		return -1;
	    	else if(o1.getPriority()<o2.getPriority())
	    		return +1;
	    	else
	    		return 0;
	    }
    }
	
	public static final LaunchingObservable getInstance() {
		return Holder.INSTANCE;
	}

	private ServiceLoader<LaunchingListener> loader;
	private List<LaunchingListener> listeners = new ArrayList<LaunchingListener>();
	
	private LaunchingObservable() {
		this.loader = ServiceLoader.load(LaunchingListener.class, LaunchingObservable.class.getClassLoader());
		if (this.loader==null || this.loader.iterator()==null || !this.loader.iterator().hasNext()) {
			this.loader = ServiceLoader.load(LaunchingListener.class, Thread.currentThread().getContextClassLoader());
		}
		List<Class<? extends LaunchingListener>>  listenersClasses = new ArrayList<Class<? extends LaunchingListener>>();
		
		List<LaunchingListener> lst = new ArrayList<LaunchingListener>();
		// load built-in listener from META-INF
		for (LaunchingListener listener : loader) {
			lst.add(listener);
		}
		
		Collections.sort(lst, new LaunchingListenerComparator());
		for (LaunchingListener launchingListener : lst) {
			listenersClasses.add(launchingListener.getClass()); 
		}

		for (Class<? extends LaunchingListener> listenerClass : listenersClasses) {
			try {
				this.listeners.add(listenerClass.newInstance());
			} catch (Exception e) {
				// TODO: add message kind
				e.printStackTrace();
			}
		}
	}
	
	public void prepareLaunching(Adapter adapter, ProcessHelper<?> processHelper, JobExecutionContext context) {
		//eventManagement.dispatch(event);
		for (LaunchingListener listener : this.listeners) {
			try {
				listener.prepareLaunching(adapter, processHelper,  context);
			} catch (Exception e) {
				// TODO add message kind
				e.printStackTrace();
			}
		}
	}
	
	public void finalizeLaunching(Adapter adapter, ProcessHelper<?> processHelper, JobExecutionContext context) {
		//eventManagement.dispatch(event);
		for (LaunchingListener listener : this.listeners) {
			try {
				listener.finalizeLaunching(adapter, processHelper,  context);
			} catch (Exception e) {
				// TODO add message kind
				e.printStackTrace();
			}
		}
	}
	
}
