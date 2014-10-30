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
package org.parallelj.launching.transport.ssh;

import java.io.IOException;

import org.parallelj.launching.LaunchingMessageKind;

@Deprecated
public class SshServer {

	private org.apache.sshd.SshServer sshd;
	private int port;
	private boolean started = false;

	public SshServer(int port) {
		this.port = port;
		sshd = org.apache.sshd.SshServer.setUpDefaultServer();
		sshd.setPort(port);

		sshd.setShellFactory(new SshShellFactory());
		initialize(this.sshd);
	}

	private void initialize(org.apache.sshd.SshServer sshd) {
		// Do nothing...
	}
	
	public void start() throws IOException {
		LaunchingMessageKind.ISSH0001.format(this.port);
		try {
			sshd.start();
			this.started=true;
			LaunchingMessageKind.ISSH0002.format(this.port);
		} catch (Exception e) {
			// Do nothing
			LaunchingMessageKind.ESSH0001.format(e);
		}
	}

	public void stop() throws InterruptedException {
		if (this.started) {
			LaunchingMessageKind.ISSH0003.format();
			sshd.stop();
		}
	}

	public boolean isStarted() {
		return started;
	}
}
