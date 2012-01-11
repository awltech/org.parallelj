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

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.rmi.NoSuchObjectException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;
import javax.management.remote.JMXConnectorServer;
import javax.management.remote.JMXConnectorServerFactory;
import javax.management.remote.JMXServiceURL;

import org.parallelj.internal.reflect.ProgramAdapter;
import org.parallelj.internal.reflect.ProgramAdapter.Adapter;
import org.parallelj.launching.LaunchingMessageKind;
import org.parallelj.launching.transport.AdaptersArguments;
import org.parallelj.launching.transport.tcp.program.ArgEntry;

/**
 * Class representing a Parallelj JMX Server for remote launching 
 */
public class JmxServer {
	private static final String DEFAULT_SERVER_URL_FORMAT = "service:jmx:rmi://%s/jndi/rmi://%s:%s/server";
	private static final String DEFAULT_BEAN_NAME_FORMAT = "%s:type=%s";

	private String host;
	private int port;
	private String serverUrlFormat;
	private String beanNameFormat;
	private Registry register = null;
	private MBeanServer mbs = null;
	private JMXConnectorServer jmxConnectorServer = null;
	private List<ObjectName> beanNames = new ArrayList<ObjectName>();

	/**
	 * Constructor for a JMX Server
	 * 
	 * @param host the host ip
	 * @param port the port the JMX Server to listen to
	 */
	public JmxServer(final String host, final int port) {
		this.host = host;
		this.port = port;
		this.serverUrlFormat = DEFAULT_SERVER_URL_FORMAT;
		this.beanNameFormat = DEFAULT_BEAN_NAME_FORMAT;
	}

	/**
	 * Start the JMX Server
	 * 
	 * @throws IOException
	 */
	public final synchronized void start() throws IOException {
		LaunchingMessageKind.IJMX0001.format(this.host, this.port);
		this.mbs = ManagementFactory.getPlatformMBeanServer();

		final String oldRmiServerName = System
				.getProperty("java.rmi.server.hostname");
		System.setProperty("java.rmi.server.hostname", this.host);

		register = LocateRegistry.createRegistry(this.port);
		if (oldRmiServerName == null) {
			final Properties props = System.getProperties();
			for (Object key : props.keySet()) {
				if (key.equals("java.rmi.server.hostname")) {
					props.remove(key);
					break;
				}
			}
		} else {
			System.setProperty("java.rmi.server.hostname", oldRmiServerName);
		}

		final String serviceURL = String.format(serverUrlFormat, this.host,
				this.host, this.port);
		LaunchingMessageKind.IJMX0002.format(serviceURL);

		final JMXServiceURL url = new JMXServiceURL(serviceURL);
		this.jmxConnectorServer = JMXConnectorServerFactory
				.newJMXConnectorServer(url, null, mbs);
		this.jmxConnectorServer.start();
	}

	/**
	 * Stop the JMX Server
	 */
	public final synchronized void stop() {
		LaunchingMessageKind.IJMX0003.format();
		unRegisterMBeans();

		try {
			if (this.jmxConnectorServer != null) {
				this.jmxConnectorServer.stop();
			}
		} catch (IOException e1) {
			// Do nothing
		}
		try {
			UnicastRemoteObject.unexportObject(register, true);
		} catch (NoSuchObjectException e) {
			// Do nothing
		}
	}

	/**
	 * Try to register standard MBean type in the JMX server
	 * 
	 * @param beanClass the class name of the MBean 
	 */
	public final boolean registerMBean(final String className) {
		if (this.mbs != null && className != null) {
			try {
				final Class<?> clazz = Class.forName(className);

				final String fqnName = clazz.getCanonicalName();
				final String domain = fqnName.substring(0, fqnName.lastIndexOf('.'));
				final String type = fqnName.substring(fqnName.lastIndexOf('.') + 1);

				final ObjectName objectName = new ObjectName(String.format(
						beanNameFormat, domain, type));
				if (!mbs.isRegistered(objectName)) {
					LaunchingMessageKind.IJMX0004.format(objectName);
					mbs.registerMBean(clazz.newInstance(), objectName);
					LaunchingMessageKind.IJMX0005.format(objectName);
				}
			} catch (Exception e) {
				LaunchingMessageKind.EJMX0004.format(className, e);
				return false;
			}
		} else {
			LaunchingMessageKind.EJMX0002.format();
			return false;
		}
		return true;
	}

	/**
	 * This method try to register a type annotated with @Program as
	 * a MBean in the JMX server
	 * 
	 * @param beanClass the class name of the Program 
	 */
	public final boolean registerProgramAsMBean(final String beanClass) {
		if (this.mbs != null && beanClass != null) {
			try {
				@SuppressWarnings("unchecked")
				final Class<? extends Adapter> clazz = (Class<? extends Adapter>) Class
						.forName(beanClass);
				final String fqnName = clazz.getCanonicalName();
				final String domain = fqnName.substring(0, fqnName.lastIndexOf('.'));
				final String type = fqnName.substring(fqnName.lastIndexOf('.') + 1);

				/*
				 * List of types annotated with @In and its Parser class:
				 * adapterArgs[0] : the attribute name adapterArgs[1] : the
				 * canonical name of the corresponding parser class
				 */
				final List<ArgEntry> adapterArgs = AdaptersArguments.getAdapterArguments(clazz);

				if (Arrays.asList(clazz.getInterfaces()).contains(
						ProgramAdapter.Adapter.class)) {
					final DynamicLegacyProgram dprogram = new DynamicLegacyProgram(
							clazz, adapterArgs);
					final ObjectName objectName = new ObjectName(String.format(
							beanNameFormat, domain, type));
					this.beanNames.add(objectName);

					if (!mbs.isRegistered(objectName)) {
						LaunchingMessageKind.IJMX0004.format(objectName);
						mbs.registerMBean(dprogram, objectName);
						LaunchingMessageKind.IJMX0005.format(objectName);
					}
				} else {
					LaunchingMessageKind.EJMX0003.format(clazz);
					return false;
				}
			} catch (ClassNotFoundException e) {
				LaunchingMessageKind.EJMX0004.format(beanClass, e);
				return false;
			} catch (MalformedObjectNameException e) {
				LaunchingMessageKind.EJMX0004.format(beanClass, e);
				return false;
			} catch (NullPointerException e) {
				LaunchingMessageKind.EJMX0004.format(beanClass, e);
				return false;
			} catch (InstanceAlreadyExistsException e) {
				LaunchingMessageKind.EJMX0004.format(beanClass, e);
				return false;
			} catch (MBeanRegistrationException e) {
				LaunchingMessageKind.EJMX0004.format(beanClass, e);
				return false;
			} catch (NotCompliantMBeanException e) {
				LaunchingMessageKind.EJMX0004.format(beanClass, e);
				return false;
			}
		} else {
			LaunchingMessageKind.EJMX0002.format();
			return false;
		}
		return true;
	}

	/**
	 * Unregister all registered MBeans in the JMX server.
	 */
	private void unRegisterMBeans() {
		if (beanNames != null) {
			for (ObjectName objectName : beanNames) {
				// unRegister the bean in the JMX server...
				try {
					mbs.unregisterMBean(objectName);
					LaunchingMessageKind.IJMX0006.format(objectName);
				} catch (MBeanRegistrationException e) {
					// Do nothing
				} catch (InstanceNotFoundException e) {
					// Do nothing
				}
			}
			beanNames.clear();
		}
	}
	
	public boolean isStarted() {
		return this.jmxConnectorServer.isActive();
	}
}
