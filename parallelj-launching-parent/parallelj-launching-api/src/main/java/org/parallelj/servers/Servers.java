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
package org.parallelj.servers;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.parallelj.internal.conf.ConfigurationService;
import org.parallelj.internal.conf.ParalleljConfigurationManager;
import org.parallelj.internal.conf.pojos.CBean;
import org.parallelj.internal.conf.pojos.CServer;
import org.parallelj.internal.conf.pojos.ParalleljConfiguration;
import org.parallelj.launching.LaunchException;
import org.parallelj.launching.Launcher;
import org.parallelj.launching.LaunchingMessageKind;

public enum Servers {
	INSTANCE;

	private final ParalleljConfiguration configuration = (ParalleljConfiguration) ConfigurationService
			.getConfigurationService().getConfigurationManager()
			.get(ParalleljConfigurationManager.class).getConfiguration();
	
	
	private final List<Server> servers = new ArrayList<>();

	public static Servers getInstance() {
		return INSTANCE;
	}
	
	public void startServers() {
		if(configuration.getServers()!=null
				&& configuration.getServers().getServer()!=null) {
			List<CServer> cServers = configuration.getServers().getServer();
			List<CBean> beans = null;
			if(configuration.getServers().getBeans()!=null
					&& configuration.getServers().getBeans().getBean()!=null) {
				beans = configuration.getServers().getBeans().getBean();
			}
			for (CServer cExtServer : cServers) {
				String clazz = cExtServer.getType();
				Server server = null;
				try {
					@SuppressWarnings("unchecked")
					Class<Server> serverClass = (Class<Server>)Class.forName(clazz, true, Servers.class.getClassLoader()); 
					Constructor<Server> c = serverClass.getConstructor(CServer.class, List.class);
					server = c.newInstance(cExtServer, beans);
					this.servers.add(server);
				} catch (InstantiationException e) {
					LaunchingMessageKind.ESERVER0001.format(clazz,e);
				} catch (IllegalAccessException e) {
					LaunchingMessageKind.ESERVER0001.format(clazz,e);
				} catch (ClassNotFoundException e) {
					LaunchingMessageKind.ESERVER0001.format(clazz,e);
				} catch (NoSuchMethodException e) {
					LaunchingMessageKind.ESERVER0001.format(clazz,e);
				} catch (SecurityException e) {
					LaunchingMessageKind.ESERVER0001.format(clazz,e);
				} catch (IllegalArgumentException e) {
					LaunchingMessageKind.ESERVER0001.format(clazz,e);
				} catch (InvocationTargetException e) {
					LaunchingMessageKind.ESERVER0001.format(clazz,e);
				}
			}
		}
		// Initialize //J Launcher (in case it's not already initialized)
		try {
			Launcher.getLauncher();
		} catch (LaunchException e) {
			LaunchingMessageKind.ELAUNCH0010.format(e);
		}
		for (Server server:servers) {
			server.start();
		}
	}
	
	public void stopServers() {
		for (Server server : this.servers) {
			server.stop();
		}
	}
	
}
