package org.parallelj.launching.internal.ext;

import org.apache.sshd.SshServer;

/**
 */
public abstract class AbstractExtension  implements Extension {

	/**
	 * Implement this method to know about init life cycle callback.
	 */
	public abstract void init() throws ExtensionException;
	
	public abstract void process(SshServer sshd) throws ExtensionException;


	/**
	 * Implement this method to know about destroy life cycle callback.
	 */
	public abstract void destroy();

	@Override
	public String toString() {
		return "[" + getClass().getSimpleName() + "]";
	}
}
