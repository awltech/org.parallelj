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

import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;

import org.parallelj.Program;
import org.parallelj.internal.conf.pojos.CBean;
import org.parallelj.internal.conf.pojos.CServer;
import org.parallelj.internal.reflect.Adapter;
import org.parallelj.launching.LaunchingMessageKind;
import org.parallelj.launching.remote.RemoteProgram;
import org.parallelj.launching.remote.RemotePrograms;
import org.parallelj.launching.transport.jmx.DynamicLegacyProgram;
import org.parallelj.servers.Server;

/**
 * Class representing a Parallelj JMX Server for remote launching
 * This implementation doesn't create JMX connector. It only register MBeans at startup
 * and unregister MBeans at shutdown.
 */
public class MinimalJmxServer extends Server {

	private static final String DEFAULT_BEAN_NAME_FORMAT = "%s:type=%s";

	private String beanNameFormat;

	private MBeanServer mbs = null;
	private List<ObjectName> beanNames = new ArrayList<ObjectName>();

	public MinimalJmxServer(CServer cServer, List<CBean> beans) {
		super(cServer, beans);
		this.beanNameFormat = DEFAULT_BEAN_NAME_FORMAT;
	}

	@Override
	public void start() {
		if (parseProperties()) {
			LaunchingMessageKind.ISERVER0001.format(this, "default platform");
			this.mbs = ManagementFactory.getPlatformMBeanServer();

			registerMBeans();
		
		} else {
			LaunchingMessageKind.ESERVER0002.format(this);
		}
	}

	@Override
	public void stop() {
		LaunchingMessageKind.ISERVER0005.format(this);
		unRegisterMBeans();
	}

	@Override
	protected boolean parseProperties() {
		if(this.server.getProperty().size()>0 || this.server.getProperties().size()>0) {
			LaunchingMessageKind.ESERVER0005.format(this,"");
			return false;
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
}
