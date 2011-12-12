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
 * Define a Command available in a TcpIpServer
 */
abstract class AbstractTcpCommand implements TcpCommand, Comparable<TcpCommand> {
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof String) {
			return (this.getType().equals((String)obj));
		}
		if (obj instanceof RemoteCommand) {
			return (this.getType().equals(((RemoteCommand)obj).name()));
		}
		if (obj instanceof TcpCommand) {
			String type = ((TcpCommand)obj).getType();
			try  {
				return (this.getType().equals(RemoteCommand.valueOf(type).name()));
			} catch (IllegalArgumentException e) {
				return false;
			} catch (NullPointerException e) {
				return false;
			}
		}
		return false;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return getType().hashCode();
	}

	/* (non-Javadoc)
	 * @see org.parallelj.launching.transport.tcp.command.TcpCommand#process(org.apache.mina.core.session.IoSession, java.lang.String[])
	 */
	public abstract String process(IoSession session, String... args);
	
	/* (non-Javadoc)
	 * @see org.parallelj.launching.transport.tcp.command.TcpCommand#getType()
	 */
	public abstract String getType();

	/* (non-Javadoc)
	 * @see org.parallelj.launching.transport.tcp.command.TcpCommand#getUsage()
	 */
	@Override
	public abstract String getUsage();
	
	/* (non-Javadoc)
	 * @see org.parallelj.launching.transport.tcp.command.TcpCommand#getPriorityUsage()
	 */
	@Override
	public abstract int getPriorityUsage();

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(TcpCommand o) {
		return o.getPriorityUsage()-this.getPriorityUsage();
	}
	
}
