package org.parallelj.launching.internal;

import org.parallelj.launching.Launch;

public interface LaunchingListener {
	
	public void prepareLaunching(Launch launch) throws Exception;
	
	public void finalizeLaunching(Launch launch) throws Exception;
	
	public int getPriority();
}
