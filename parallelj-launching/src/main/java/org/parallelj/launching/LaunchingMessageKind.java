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
	
	
	/*
	 * Information messages for Simple Launching
	 */
	
	/**
	 * An Error occurred
	 */
	@Format("An Error occurred when running the Program %s!")
	ELAUNCH0002,
	
	/**
	 * An Error occurred
	 */
	@Format("An Error occurred when launching the Program %s!")
	ELAUNCH0003,
	
	/**
	 * An Error occurred when launching the Program %s: field '%s' is annotated with @In but no setter method is defined for this field!!! This program won't appear on remote client!!!
	 */
	@Format("An Error occurred when launching the Program %s: field '%s' is annotated with @In but no setter method is defined for this field!!! This program won't appear on remote client!!!")
	ELAUNCH0004,
	
	/**
	 * An Error occurred when launching the Program %s: field '%s' is annotated with @Out but no getter method is defined for this field!!! This program won't appear on remote client!!!
	 */
	@Format("An Error occurred when launching the Program %s: field '%s' is annotated with @Out but no getter method is defined for this field!!! This program won't appear on remote client!!!")
	ELAUNCH0005,
	
	/**
	 * An Error occurred when launching the Program %s: field '%s' is annotated with @OnError but no setter method is defined for this field!!! Program not launched!!!
	 */
	@Format("An Error occurred when launching the Program %s: field '%s' is annotated with @OnError but no setter method is defined for this field!!! Program not launched!!!")
	ELAUNCH0006,
	
	/**
	 * Error while launching Program %s. Program not launched
	 */
	@Format("Error invoking Program %s: %s")
	ELAUNCH0007,
	
	/**
	 * Error invoking Program %s. Program not launched
	 */
	@Format("Error invoking Program %s. Program not launched")
	ELAUNCH0008,
	
	/**
	 * Error invoking Program %s. Program has already been launched. Use another Program instance to launch it again!
	 */
	@Format("Error invoking Program %s. Program has already been launched. Use another Program instance to launch it again!")
	ELAUNCH0009,
	
	/**
	 * Error occured during Servers launch!
	 */
	@Format("Error occured during Servers launch!")
	ELAUNCH0010,
	
	/**
	 * Starting Servers
	 */
	@Format("Starting Servers...")
	ILAUNCH0001,
	
	/**
	 * Error occured during default ExecutorService instanciation
	 */
	@Format("Error occured during default ExecutorService instanciation!")
	ELAUNCH0011,
	
	/**
	 * Error occured during ExecutorService instanciation
	 */
	@Format("Error occured during ExecutorService instanciation!")
	ELAUNCH0012,
	
	/*
	 * Information messages for TcpIp
	 */
	
	/**
	 * Starting TcpIpServer on [%s:%s]
	 */
	@Deprecated
	@Format("Starting the TcpIpServer on [%s:%s]")
	ITCPIP0001,
	
	/**
	 * Stopping TcpIpServer
	 */
	@Deprecated
	@Format("Stoping the TcpIpServer")
	ITCPIP0002,
	
	/*
	 * Error messages for TcpIp
	 */
	
	/**
	 * TcpIpServer can't be started
	 */
	@Deprecated
	@Format("TcpIpServer can't be started")
	ETCPIP0001,
	
	/*
	 * Information messages for JMX
	 */
	
	/**
	 * Starting the JmxServer on [%s:%s]
	 */
	@Deprecated
	@Format("Starting the JmxServer on [%s:%s]")
	IJMX0001,
	
	/**
	 * JmxServer started! [%s]
	 */
	@Deprecated
	@Format("JmxServer started! [%s]")
	IJMX0002,
	
	/**
	 * Stoping the JmxServer
	 */
	@Deprecated
	@Format("Stoping the JmxServer")
	IJMX0003,
	
	/**
	 * Registering MBean %s...
	 */
	@Format("Registering MBean %s...")
	IJMX0004,
	
	/**
	 * MBean %s registered
	 */
	@Format("MBean %s registered")
	IJMX0005,
	
	/**
	 * MBean %s unregistered
	 */
	@Format("MBean %s unregistered")
	IJMX0006,
	
	/*
	 * Error messages for JMX
	 */
	
	/**
	 * JmxServer can't be started
	 */
	@Deprecated
	@Format("JmxServer can't be started")
	EJMX0001,
	
	/**
	 * Can't register any MBeans as no Server is started
	 */
	@Format("Can't register any MBeans no Server is started")
	EJMX0002,
	
	/**
	 * Can't register MBean : is class %s annotated with @Program ?
	 */
	@Format("Can't register MBean : is class %s annotated with @Program ?")
	EJMX0003,

	/**
	 * Error loading class %s for MBean registration
	 */
	@Format("Error loading class %s for MBean registration")
	EJMX0004,
	
	/*
	 * Information messages for Launching 
	 */
	
	/**
	 * Program %s launched! jobId: %s 
	 */
	@Format("Program %s launched! jobId: %s ")
	ILAUNCH0002,
	
	/**
	 * Program %s with jobId %s is terminated with status %s! Return code: [%s]
	 */
	@Format("Program %s with jobId %s is terminated with status %s! Return code: [%s]")
	ILAUNCH0003,
	
	/**
	 * Error starting ParallelJ Servlet listener
	 */
	@Format("Error starting ParallelJ Servlet listener")
	EWLAUNCH0001,
	
	/**
	 * ParallelJ Servlet listener failed to shutdown cleanly
	 */
	@Format("ParallelJ Servlet listener failed to shutdown cleanly")
	EWLAUNCH0002,

	/*
	 * Error messages for remote access
	 */
	
	/**
	 * Can't find class %
	 */
	@Format("Can't find class %")
	EREMOTE0001,
	
	/**
	 * Can't parse command line when launching program [...]
	 */
	@Format("Can't parse command line when launching program [%s %s %s %s %s %s %s %s %s %s %s %s %s]")
	EREMOTE0002,
	
	/**
	 * Can't instanciate class ?
	 */
	@Format("Can't instanciate class ?")
	EREMOTE0003,
	
	/**
	 * Id %s is out of range! No Program launched!
	 */
	@Format("Id %s is out of range! No Program launched!")
	EREMOTE0004,
	
	/**
	 * Invalid arguments for Program %s. Number of expected arguments is %s! Program not launched!
	 */
	@Format("Invalid arguments for Program %s. Number of expected arguments is %s! Program not launched!")
	EREMOTE0005,
	
	/**
	 * Illegal argument value. Program not launched!
	 */
	@Format("Illegal argument value %s. Program not launched!")
	EREMOTE0006,
	
	/**
	 * Error with Parser %s - Program not launched!
	 */
	@Format("Error with Parser %s - Program not launched!")
	EREMOTE0007,
	
	/**
	 * Invalid option value
	 */
	@Format("Invalid option value: %s - %s!")
	EREMOTE0008,
	
	/**
	 * An error occurred
	 */
	@Format("An error occurred.")
	EREMOTE0009,
	
	/**
	 * Invalid arguments %s for Program %s. Parser error!
	 */
	@Format("Invalid arguments %s for Program %s. Parser error!")
	EREMOTE0010,
	
	/**
	 * Invalid arguments %s for Program %s. Argument should be Key-Value pair!
	 */
	@Format("Invalid arguments %s for Program %s. Argument should be Key-Value pair!")
	EREMOTE0011,
	
	/**
	 * Invalid arguments %s for Program %s. Unknown argument name!
	 */
	@Format("Invalid arguments %s for Program %s. Unknown argument name: %s !")
	EREMOTE0012,
	
	//TODO: Warning message
	/**
	 * Invalid arguments %s for Program %s. Argument value(s) should be in quotes!
	 */
	@Format("Invalid number of arguments for Program %s. Please consider enclosing Argument value(s) in quotes!")
	WREMOTE001,
	
	/*
	 * Information messages for Ssh
	 */
	
	/**
	 * Starting SshServer on [%s]
	 */
	@Deprecated
	@Format("Starting the SshServer on [%s]")
	ISSH0001,
	
	/**
	 * SshServer started on [%s]
	 */
	@Deprecated
	@Format("SshServer started on [%s]")
	ISSH0002,
	
	/**
	 * Stopping SshServer
	 */
	@Deprecated
	@Format("Stopping the SshServer")
	ISSH0003,
	
	/*
	 * Error messages for Ssh
	 */
	
	/**
	 * SshServer can't be started
	 */
	@Deprecated
	@Format("SshServer can't be started")
	ESSH0001,
	
	/**
	 * SshServer failed to shutdown cleanly
	 */
	@Deprecated
	@Format("SshServer failed to shutdown cleanly")
	ESSH0002,
	
	/*
	 * Other Error messages
	 */
	/**
	 * @Info Loading [%s].
	 */
	@Format("Loading [%s].")
	IEXT001,
	
	/**
	 * @Error Unable to initialize [%s].
	 */
	@Format("Unable to initialize [%s].")
	EEXT002,
	
	/**
	 * @Error Error processing [%s].
	 */
	@Format("Error processing [%s].")
	EEXT003, 
	
	
	@Format("Service Loading from ParallelJ Launched enabled with cache: [%s]")
	ICACHESPI001,
	
	@Format("Update configuration successfully done.")
	ICONF001,
	
	@Format("Reload configuration successfully done.")
	ICONF002,

	/**
	 * Unable to instanciate server [%s]
	 */
	@Format("Unable to instanciate server [%s]")
	ESERVER0001,
	
	/**
	 * Unable to start server  [%s]
	 */
	@Format("Unable to start server [%s]")
	ESERVER0002,
	
	/**
	 * An error occurred when starting server [%s] [%s]
	 */
	@Format("An error occurred when starting server [%s] [%s]")
	ESERVER0003,
	
	/**
	 * An error occured when stopping server [%s]
	 */
	@Format("An error occured when stopping server [%s]")
	ESERVER0004,
	
	/**
	 * Invalid Server configuration [%s]: [%s]
	 */
	@Format("Invalid Server configuration [%s] [%s]:[%s]")
	ESERVER0005,
	
	/**
	 * Invalid extension Server configuration [%s]: [%s]
	 */
	@Format("Invalid extension Server configuration [%s] [%s]:[%s]")
	ESERVER0006,
	
	/**
	 * "Starting server [%s] on [%s]..."
	 */
	@Format("Starting server [%s] on [%s]...")
	ISERVER0001,
	
	/**
	@Format("Starting server [%s] on [%s] [%s]...")
	 */
	@Format("Starting server [%s] on [%s] [%s]...")
	ISERVER0002,
	
	/**
	 * "Server [%s] started"
	 */
	@Format("Server [%s] started !")
	ISERVER0003,
	
	/**
	 * "Server [%s] started ! Info: [%s]"
	 */
	@Format("Server [%s] started ! Info: [%s]")
	ISERVER0004,
	
	/**
	 * "Stopping server [%s]"
	 */
	@Format("Stopping server [%s]...")
	ISERVER0005,
	
	/**
	 * "Server [%s] stopped."
	 */
	@Format("Server [%s] stopped.")
	ISERVER0006//,
	

;
	

	/**
	 * Method used to format a message
	 * 
	 * @param args
	 *            the arguments used to format the message
	 * @return the formatted message
	 */
	public String format(final Object... args) {
		// delegates to formatter
		return formatter.print(this, args);
	}
	
	public String getFormatedMessage(final Object... args) {
		final String format[] = formatter.getFormatedMessage(this, args).split("\t");
		if (format.length>=2) {
			return format[1];
		}
		return format[0];
	}
	
	private static Formatter<LaunchingMessageKind> formatter = new Formatter<LaunchingMessageKind>(LaunchingMessageKind.class);

}
