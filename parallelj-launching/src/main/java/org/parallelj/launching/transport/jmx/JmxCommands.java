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
package org.parallelj.launching.transport.jmx;

import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

import org.parallelj.launching.internal.spi.CacheableServiceLoader;


/**
 * Entry point for all available commands for remote launching using Jmx
 *
 */
public final class JmxCommands {
	
	/**
	 * Available commands
	 */
	private final Map<String, JmxCommand> commands = new HashMap<String, JmxCommand>();
	
	/**
	 * The instance of JmxCommands
	 */
	private static JmxCommands instance = new JmxCommands();

	/**
	 * Default constructor
	 */
	private JmxCommands() {
		// Search for available commands
		ServiceLoader<JmxCommand> loader = CacheableServiceLoader.INSTANCE.load(JmxCommand.class, JmxCommands.class.getClassLoader());
		if (loader==null || loader.iterator()==null || !loader.iterator().hasNext()) {
			loader = CacheableServiceLoader.INSTANCE.load(JmxCommand.class, Thread.currentThread().getContextClassLoader());
		}
		for (JmxCommand command:loader) {
			this.commands.put(command.getType(), command);
		}
	}

	/**
	 * Get the available commands
	 * 
	 * @return a Map of all available JmxCommand
	 */
	public static Map<String, JmxCommand> getCommands() {
		return instance.commands;
	}
}
