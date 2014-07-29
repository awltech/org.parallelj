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
package org.parallelj.servers.jmx;

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
import org.parallelj.internal.conf.pojos.CBean;
import org.parallelj.internal.conf.pojos.CProperty;
import org.parallelj.internal.conf.pojos.CServer;
import org.parallelj.internal.reflect.Adapter;
import org.parallelj.launching.LaunchingMessageKind;
import org.parallelj.launching.remote.RemoteProgram;
import org.parallelj.launching.remote.RemotePrograms;
import org.parallelj.launching.transport.jmx.DynamicLegacyProgram;
import org.parallelj.servers.Server;

/**
 * Class representing a Parallelj JMX Server for remote launching
 */
public class GenericJmxServer extends Server {

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
	private String serviceURL;

	public GenericJmxServer(CServer cServer, List<CBean> beans) {
		super(cServer, beans);
		this.serverUrlFormat = DEFAULT_SERVER_URL_FORMAT;
		this.beanNameFormat = DEFAULT_BEAN_NAME_FORMAT;
	}

	/**
	 * Constructor for a JMX Server
	 * 
	 * @param host
	 *            the host ip
	 * @param port
	 *            the port the JMX Server to listen to
	 */
	public GenericJmxServer(final String host, final int port) {
		super(null, null);
		this.host = host;
		this.port = port;
	}

	@Override
	public void start() {
		try {
			if (parseProperties()) {
				LaunchingMessageKind.ISERVER0002.format(this, this.host, this.port);
				this.mbs = ManagementFactory.getPlatformMBeanServer();

				final String oldRmiServerName = System
						.getProperty("java.rmi.server.hostname");
				System.setProperty("java.rmi.server.hostname", this.host);

				this.register = LocateRegistry.createRegistry(this.port);
				if (oldRmiServerName == null) {
					final Properties props = System.getProperties();
					for (Object key : props.keySet()) {
						if (key.equals("java.rmi.server.hostname")) {
							props.remove(key);
							break;
						}
					}
				} else {
					System.setProperty("java.rmi.server.hostname",
							oldRmiServerName);
				}

				this.serviceURL = String.format(this.serverUrlFormat,
						this.host, this.host, this.port);

				final JMXServiceURL url = new JMXServiceURL(this.serviceURL);
				this.jmxConnectorServer = JMXConnectorServerFactory
						.newJMXConnectorServer(url, null, mbs);
				this.jmxConnectorServer.start();

				registerMBeans();
			
			} else {
				LaunchingMessageKind.ESERVER0002.format(this);
			}
		} catch (IOException e) {
			LaunchingMessageKind.ESERVER0005.format(this,"");
		}
		LaunchingMessageKind.ISERVER0004.format(this,this.serviceURL);
	}

	@Override
	public void stop() {
		LaunchingMessageKind.ISERVER0005.format(this);
		unRegisterMBeans();

		try {
			if (this.jmxConnectorServer != null) {
				this.jmxConnectorServer.stop();
			}
		} catch (IOException e) {
			LaunchingMessageKind.ESERVER0004.format(this, e);
		}
		try {
			UnicastRemoteObject.unexportObject(register, true);
		} catch (NoSuchObjectException e) {
			LaunchingMessageKind.ESERVER0004.format(this, e);
		}
	}

	@Override
	protected boolean parseProperties() {
		for (CProperty property : this.server.getProperty()) {
			switch (property.getName()) {
			case "host":
				this.host = property.getValue();
				if (this.host == null || this.host.trim().length() == 0) {
					LaunchingMessageKind.ESERVER0005.format(this, "invalid host value",property.getValue());
					return false;
				}
				break;
			case "port":
				try {
					this.port = Integer.parseInt(property.getValue());
				} catch (NumberFormatException e) {
					LaunchingMessageKind.ESERVER0005.format(this, "invalid port value");
					return false;
				}
				break;
			default:
				break;
			}
		}
		return true;
	}

	public void registerMBeans() {
		// Scan all defined Program in parallej.xml
		if (beans != null
				&& beans.size()>0) {
			for (CBean bean : beans) {
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
					Adapter.class)) {
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
		} catch (MalformedObjectNameException
				|NullPointerException
				|InstanceAlreadyExistsException 
				|MBeanRegistrationException
				|NotCompliantMBeanException e) {
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
