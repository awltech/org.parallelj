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
	 * Information messages for TcpIp
	 */
	@Format("Starting the TcpIpServer on [%s:%s]")
	ITCPIP0001,
	
	@Format("Stoping the TcpIpServer")
	ITCPIP0002,
	
	/**
	 * Error messages for TcpIp
	 */
	@Format("TcpIpServer can't be started")
	ETCPIP0001,
	
	/**
	 * Information messages for JMX
	 */
	@Format("Starting the JmxServer on [%s:%s]")
	IJMX0001,
	
	@Format("JmxServer started! [%s]")
	IJMX0002,
	
	@Format("Stoping the JmxServer")
	IJMX0003,
	
	@Format("Registering MBean %s...")
	IJMX0004,
	
	@Format("MBean %s registered")
	IJMX0005,
	
	@Format("MBean %s unregistered")
	IJMX0006,
	
	/**
	 * Error messages for JMX
	 */
	@Format("JmxServer can't be started")
	EJMX0001,
	
	@Format("Can't register any MBeans as JmxServer is not started")
	EJMX0002,
	
	@Format("Can't register MBean : is class %s annotated with @Program ?")
	EJMX0003,

	@Format("Error loading class %s for MBean registration")
	EJMX0004,
	
	/**
	 * Information messages for Quartz 
	 */
	@Format("QuartzScheduler started")
	IQUARTZ0001,
	
	/**
	 * Error messages for Quartz 
	 */
	@Format("Error starting Quartz Scheduler")
	EQUARTZ0001,
	
	@Format("Quartz Scheduler failed to shutdown cleanly")
	EQUARTZ0002,
	
	@Format("Error invoking Program %. Program not launched")
	EQUARTZ0003,
	
	/**
	 * Error messages for remote access
	 */
	@Format("Can't find class %")
	EREMOTE0001,
	
	@Format("Can't parse command line when launching program [%s %s %s %s %s %s %s %s %s %s %s %s %s]")
	EREMOTE0002,
	
	@Format("Can't instanciate class ?")
	EREMOTE0003,
	
	@Format("Id %s is out of range! No Program launched!")
	EREMOTE0004,
	
	@Format("Invalid arguments for Program %s. Number of expected arguments is %s! Program not launched!")
	EREMOTE0005,
	
	@Format("Illegal argument value. Program not launched!")
	EREMOTE0006,
	
	@Format("Error with Parser %s - Program not launched!")
	EREMOTE0007;
	

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
