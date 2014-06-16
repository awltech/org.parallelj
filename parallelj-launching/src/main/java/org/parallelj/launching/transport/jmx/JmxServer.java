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

import org.parallelj.Program;
import org.parallelj.internal.conf.ConfigurationService;
import org.parallelj.internal.conf.ParalleljConfigurationManager;
import org.parallelj.internal.conf.pojos.CBean;
import org.parallelj.internal.conf.pojos.ParalleljConfiguration;
import org.parallelj.internal.reflect.ProgramAdapter;
import org.parallelj.internal.reflect.ProgramAdapter.Adapter;
import org.parallelj.launching.LaunchingMessageKind;
import org.parallelj.launching.remote.RemoteProgram;
import org.parallelj.launching.remote.RemotePrograms;

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

	// Get the configuration
	final ParalleljConfiguration configuration = (ParalleljConfiguration) ConfigurationService
			.getConfigurationService().getConfigurationManager()
			.get(ParalleljConfigurationManager.class).getConfiguration();
	
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
		registerMBeans();
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

	public void registerMBeans() {
		// Scan all defined Program in parallej.xml
		if (configuration.getServers().getBeans() != null
				&& configuration.getServers().getBeans().getBean() != null) {
			for (CBean bean : configuration.getServers().getBeans()
					.getBean()) {
				
				try {
					Class<?> clazz = (Class<?>) Class.forName(bean.getClazz());
					if (clazz.isAnnotationPresent(Program.class)) {
						// Register the Program as MBean
						this.registerProgramAsMBean(clazz);
					} else {
						this.registerMBean(clazz);
					}
				} catch (ClassNotFoundException e) {
					LaunchingMessageKind.EJMX0004.format(bean.getClazz(), e);
				}
			}
		}
		
	}
	
	/**
	 * Try to register standard MBean type in the JMX server
	 * 
	 * @param beanClass the class name of the MBean 
	 */
	public final boolean registerMBean(final Class<?> clazz) {
		if (this.mbs != null && clazz != null) {
			try {
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
				this.beanNames.add(objectName);
			} catch (Exception e) {
				LaunchingMessageKind.EJMX0004.format(clazz.getCanonicalName(), e);
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
	public final boolean registerProgramAsMBean(final Class<?> clazz) {
		RemoteProgram remoteProgram = RemotePrograms.getRemoteProgram(clazz.getCanonicalName());
		if (remoteProgram==null) {
			LaunchingMessageKind.EJMX0003.format(clazz.getCanonicalName());
			return false;
		}
		if (this.mbs == null) {
			LaunchingMessageKind.EJMX0002.format();
			return false;
		}
		try {
			final String fqnName = remoteProgram.getAdapterClass().getCanonicalName();
			final String domain = fqnName.substring(0, fqnName.lastIndexOf('.'));
			final String type = fqnName.substring(fqnName.lastIndexOf('.') + 1);

			if (Arrays.asList(clazz.getInterfaces()).contains(
					ProgramAdapter.Adapter.class)) {
				final DynamicLegacyProgram dprogram = new DynamicLegacyProgram(
						remoteProgram);
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
		} catch (MalformedObjectNameException e) {
			LaunchingMessageKind.EJMX0004.format(clazz.getCanonicalName(), e);
			return false;
		} catch (NullPointerException e) {
			LaunchingMessageKind.EJMX0004.format(clazz.getCanonicalName(), e);
			return false;
		} catch (InstanceAlreadyExistsException e) {
			LaunchingMessageKind.EJMX0004.format(clazz.getCanonicalName(), e);
			return false;
		} catch (MBeanRegistrationException e) {
			LaunchingMessageKind.EJMX0004.format(clazz.getCanonicalName(), e);
			return false;
		} catch (NotCompliantMBeanException e) {
			LaunchingMessageKind.EJMX0004.format(clazz.getCanonicalName(), e);
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
