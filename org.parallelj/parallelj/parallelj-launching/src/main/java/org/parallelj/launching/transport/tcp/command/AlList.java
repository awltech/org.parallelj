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

/**
 * Alias for the ListPrograms TcpCommand
 *
 */
public class AlList extends ListPrograms {

	private static final int PRIORITY=89;
	private final String usage = "                              ll : Lists available programs and their associated IDs.";

	/* (non-Javadoc)
	 * @see org.parallelj.launching.transport.tcp.command.ListPrograms#getType()
	 */
	public final String getType() {
		return "ll";
	}
	
	/* (non-Javadoc)
	 * @see org.parallelj.launching.transport.tcp.command.AbstractTcpCommand#getUsage()
	 */
	@Override
	public final String getUsage() {
		return this.usage; 
	}

	/* (non-Javadoc)
	 * @see org.parallelj.launching.transport.tcp.command.ListPrograms#getPriorityUsage()
	 */
	@Override
	public final int getPriorityUsage() {
		return PRIORITY;
	}
}
