/*
 *     ParallelJ, framework for parallel computing
 *
 *     Copyright (C) 2010 Atos Worldline or third-party contributors as
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
package org.parallelj.launching.transport.tcp.command;

import org.apache.mina.core.session.IoSession;

/**
 * Stop TcpCommand
 *
 */
public class Stop extends AbstractTcpCommand {

	private static final int PRIORITY=0;
	private final String usage = "                      stop -id x : stop the program with ID x";

	/* (non-Javadoc)
	 * @see org.parallelj.launching.transport.tcp.command.AbstractTcpCommand#process(org.apache.mina.core.session.IoSession, java.lang.String[])
	 */
	@Override
	public final String process(IoSession session, String... args) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.parallelj.launching.transport.tcp.command.AbstractTcpCommand#getType()
	 */
	public final String getType() {
		return RemoteCommand.stop.name();
	}

	/* (non-Javadoc)
	 * @see org.parallelj.launching.transport.tcp.command.AbstractTcpCommand#getUsage()
	 */
	@Override
	public final String getUsage() {
		return this.usage;
	}

	@Override
	public final int getPriorityUsage() {
		return PRIORITY;
	}
}
