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

import org.apache.mina.core.session.IoSession;

/**
 * Define a Command available in a TcpIpServer for remote launching
 */
public interface TcpCommand {

	/**
	 * Process the command and return the result as a String to be print to the
	 * client
	 * 
	 * @param session
	 * @param args
	 * @return
	 */
	String process(IoSession session, String... args);

	/**
	 * Return the type of the command
	 * 
	 * @return
	 */
	String getType();
	
	/**
	 * Return a String explaining the usage of the command
	 * 
	 * @return the usage of the command
	 */
	String getUsage();

	/**
	 * Return a int representing a priority for the usage of the command
	 * 
	 * @return the usage priority of the command
	 */
	int getPriorityUsage();

}
