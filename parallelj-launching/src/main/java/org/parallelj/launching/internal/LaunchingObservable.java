package org.parallelj.launching.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ServiceLoader;

import org.parallelj.launching.Launch;

public class LaunchingObservable {

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

	
	private static final ServiceLoader<LaunchingListener> loader = ServiceLoader.load(LaunchingListener.class);
	private static final List<Class<? extends LaunchingListener>> listenersClasses = new ArrayList<Class<? extends LaunchingListener>>();

	static {
		List<LaunchingListener> lst = new ArrayList<LaunchingListener>();
		// load built-in listener from META-INF
		for (LaunchingListener listener : loader) {
			lst.add(listener);
		}
		
		Collections.sort(lst, new LaunchingListenerComparator());
		for (LaunchingListener launchingListener : lst) {
			listenersClasses.add(launchingListener.getClass()); 
		}
	}
	
	private List<LaunchingListener> listeners = new ArrayList<LaunchingListener>();
	
	public LaunchingObservable() {
		for (Class<? extends LaunchingListener> listenerClass : listenersClasses) {
			try {
				listeners.add(listenerClass.newInstance());
			} catch (Exception e) {
				// TODO: add message kind
				e.printStackTrace();
			}
		}
	}
	
	public void prepareLaunching(Launch<?> launch) {
		for (LaunchingListener listener : this.listeners) {
			try {
				listener.prepareLaunching(launch);
			} catch (Exception e) {
				// TODO: add message kind
				e.printStackTrace();
			}
		}
	}
	
	public void finalizeLaunching(Launch<?> launch) {
		for (LaunchingListener listener : this.listeners) {
			try {
				listener.finalizeLaunching(launch);
			} catch (Exception e) {
				// TODO: add message kind
				e.printStackTrace();
			}
		}
	}
	
}
