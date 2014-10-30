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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ServiceLoader;

import org.parallelj.launching.Launch;
import org.parallelj.launching.internal.spi.CacheableServiceLoader;

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
		this.loader = CacheableServiceLoader.INSTANCE.load(LaunchingListener.class, LaunchingObservable.class.getClassLoader());
		if (this.loader==null || this.loader.iterator()==null || !this.loader.iterator().hasNext()) {
			this.loader = CacheableServiceLoader.INSTANCE.load(LaunchingListener.class, Thread.currentThread().getContextClassLoader());
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
	
	public synchronized void prepareLaunching(Launch<?> launch) {
		for (LaunchingListener listener : this.listeners) {
			try {
				listener.prepareLaunching(launch);
			} catch (Exception e) {
				// TODO: add message kind
				e.printStackTrace();
			}
		}
	}
	
	public synchronized void finalizeLaunching(Launch<?> launch) {
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
