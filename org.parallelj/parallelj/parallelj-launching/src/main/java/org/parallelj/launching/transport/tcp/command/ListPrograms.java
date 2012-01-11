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

import java.util.List;

import org.apache.mina.core.session.IoSession;
import org.parallelj.launching.transport.tcp.TcpIpHandlerAdapter;
import org.parallelj.launching.transport.tcp.command.option.IOption;
import org.parallelj.launching.transport.tcp.program.ArgEntry;
import org.parallelj.launching.transport.tcp.program.TcpIpProgram;
import org.parallelj.launching.transport.tcp.program.TcpIpPrograms;

/**
 * ListProgram TcpCommand
 *
 */
public class ListPrograms extends AbstractTcpCommand {

	private static final int PRIORITY=90;
	private static final String USAGE = "list : Lists available programs and their associated IDs.";

	/* (non-Javadoc)
	 * @see org.parallelj.launching.transport.tcp.command.AbstractTcpCommand#process(org.apache.mina.core.session.IoSession, java.lang.String[])
	 */
	@Override
	public final String process(final IoSession session, final String... args) {
		final List<TcpIpProgram> programs = TcpIpPrograms.getTcpIpPrograms();
		final StringBuffer stb = new StringBuffer(20);
		for (int index = 0; index < programs.size(); index++) {
			stb.append("id:").append(index).append(' ').append(programs.get(index).getAdapterClass().getCanonicalName())
					.append(" args:[");
			for (ArgEntry argEntry : programs.get(index).getArgEntries()) {
				stb.append(argEntry.getName()).append(':')
						.append(argEntry.getType().getCanonicalName())
						.append(' ');
			}
			stb.append("]\n\r");
		}
		session.write(stb.toString());
		return null;
	}

	/* (non-Javadoc)
	 * @see org.parallelj.launching.transport.tcp.command.AbstractTcpCommand#getType()
	 */
	public String getType() {
		return RemoteCommand.list.name();
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

	@Override
	public Class<? extends IOption> getOptionClass() {
		return null;
	}

	@Override
	public List<IOption> getOptions() {
		return null;
	}

	@Override
	public String getHelp() {
		return this.getUsage()+TcpIpHandlerAdapter.ENDLINE;
	}

}
