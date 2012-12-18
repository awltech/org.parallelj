package org.parallelj.ssh;

import org.apache.sshd.SshServer;
import org.parallelj.launching.internal.ext.Extension;
import org.parallelj.launching.internal.ext.ExtensionException;


public interface SshExtension extends Extension {

	public void process(SshServer sshd) throws ExtensionException;

}
