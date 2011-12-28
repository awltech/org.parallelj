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
package org.parallelj.launching.transport.tcp.command;

import java.util.Arrays;

import org.apache.mina.core.session.IoSession;
import org.parallelj.launching.transport.tcp.TcpIpCommands;
import org.parallelj.launching.transport.tcp.TcpIpHandlerAdapter;

/**
 * Help TcpCommand
 *
 */
public class Help extends AbstractTcpCommand {
	
	private static final int PRIORITY=100;
	private static final String USAGE = "                            help : Print this help message";

	private String message;
	
	/* (non-Javadoc)
	 * @see org.parallelj.launching.transport.tcp.command.AbstractTcpCommand#process(org.apache.mina.core.session.IoSession, java.lang.String[])
	 */
	public final synchronized String process(IoSession session, String... args) {
		if (this.message == null) {
			StringBuffer strb = new StringBuffer();
			// Get all available Commands and get its usage
			TcpCommand[] cmds = TcpIpCommands.getCommands().values().toArray(new TcpCommand[]{});
			Arrays.sort(cmds);
			for (TcpCommand cmd : cmds) {
				String usage = cmd.getUsage(); 
				if (usage!=null && usage.length()>0) {
					strb.append(usage).append(TcpIpHandlerAdapter.ENDLINE);
				}
			}
			this.message=strb.toString();
		}
		return this.message;
	}
	
	/* (non-Javadoc)
	 * @see org.parallelj.launching.transport.tcp.command.AbstractTcpCommand#getType()
	 */
	public String getType() {
		return RemoteCommand.help.name();
	}

	/* (non-Javadoc)
	 * @see org.parallelj.launching.transport.tcp.command.AbstractTcpCommand#getUsage()
	 */
	@Override
	public String getUsage() {
		return USAGE;
	}

	/* (non-Javadoc)
	 * @see org.parallelj.launching.transport.tcp.command.AbstractTcpCommand#getPriorityUsage()
	 */
	@Override
	public int getPriorityUsage() {
		return PRIORITY;
	}

}
