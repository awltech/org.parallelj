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

import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;


/**
 * Entry point for all available commands for remote launching
 *
 */
public final class TcpIpCommands {
	
	/**
	 * Available commands
	 */
	private final Map<String, TcpCommand> commands = new HashMap<String, TcpCommand>();
	
	/**
	 * The instance of TcpIpCommands
	 */
	private static TcpIpCommands instance = new TcpIpCommands();

	/**
	 * Default constructor
	 */
	private TcpIpCommands() {
		// Search for available commands
		final ServiceLoader<TcpCommand> loader = ServiceLoader.load(TcpCommand.class);
		for (TcpCommand command:loader) {
			this.commands.put(command.getType(), command);
		}
	}

	/**
	 * Get the available commands
	 * 
	 * @return a Map of all available TcpIpCommand
	 */
	public static Map<String, TcpCommand> getCommands() {
		return instance.commands;
	}
}
