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

package org.parallelj.launching;

import org.parallelj.internal.util.Formatter;
import org.parallelj.internal.util.Formatter.Format;

/**
 * This enumeration contains the types of messages logged by //J launching.
 * 
 * @author Atos Worldline
 * @since 0.4.0
 */
public enum LaunchingMessageKind {
	
	/**
	 * 
	 */
	@Format("Starting the TcpIpServer on [%s:%s]")
	I0001,
	
	/**
	 * 
	 */
	@Format("Stoping the TcpIpServer")
	I0002,
	
	/**
	 * 
	 */
	@Format("Starting the JmxServer on [%s:%s]")
	I0003,
	
	/**
	 * 
	 */
	@Format("JmxServer started! [%s]")
	I0004,
	
	
	/**
	 * 
	 */
	@Format("Stoping the JmxServer")
	I0005,
	
	/**
	 * 
	 */
	@Format("Registering MBean %s...")
	I0006,
	
	
	/**
	 * 
	 */
	@Format("MBean %s registered")
	I0007,
	
	/**
	 * 
	 */
	@Format("MBean %s unregistered")
	I0008,
	
	/**
	 * 
	 */
	@Format("QuartzScheduler started")
	I0009,
	
	
	/**
	 * Error: Can not start the TciIpServer
	 */
	@Format("TcpIpServer can't be started")
	E0001,
	
	/**
	 * Error: Can not start the JmxServer
	 */
	@Format("JmxServer can't be started")
	E0002,
	
	/**
	 * Error: Can't register MBean
	 */
	@Format("Can't register any MBeans as JmxServer is not started")
	E0003,
	
	/**
	 * Error: Can't register MBean
	 */
	@Format("Can't register MBean : is class %s annotated with @Program ?")
	E0004,
	/**
	 * Error: Can't register MBean
	 */
	@Format("Error loading class %s for MBean registration")
	E0005,
	
	/**
	 * Error: Can't start QuartzScheduler
	 */
	@Format("Error starting Quartz Scheduler")
	E0006,
	
	/**
	 * Error: Quartz Scheduler failed to shutdown cleanly
	 */
	@Format("Quartz Scheduler failed to shutdown cleanly")
	E0007;
	
	/**
	 * Method used to format a message
	 * 
	 * @param args
	 *            the arguments used to format the message
	 * @return the formatted message
	 */
	public String format(Object... args) {
		// delegates to formatter
		return formatter.print(this, args);
	}
	
	static Formatter<LaunchingMessageKind> formatter = new Formatter<LaunchingMessageKind>(LaunchingMessageKind.class);

}
