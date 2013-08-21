package org.parallelj.launching.internal;

import org.parallelj.launching.Launch;

public abstract class AbstractLaunchingListener implements LaunchingListener {

	@Override
	public abstract void prepareLaunching(Launch launch) throws Exception;

	@Override
	public abstract void finalizeLaunching(Launch launch) throws Exception;

	@Override
	public abstract int getPriority();

}
