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
package org.parallelj.servers.ssh;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.sshd.common.NamedFactory;
import org.apache.sshd.server.UserAuth;
import org.apache.sshd.server.auth.UserAuthPublicKey;
import org.parallelj.internal.conf.pojos.CBean;
import org.parallelj.internal.conf.pojos.CProperties;
import org.parallelj.internal.conf.pojos.CProperty;
import org.parallelj.internal.conf.pojos.CServer;
import org.parallelj.launching.LaunchingMessageKind;
import org.parallelj.launching.internal.ext.ExtensionException;
import org.parallelj.launching.transport.ssh.SshShellFactory;
import org.parallelj.servers.Server;
import org.parallelj.servers.ServerExtension;

public class SshServer extends Server {

	private org.apache.sshd.SshServer sshd;
	private int port;
	private boolean started = false;
	
	
	List<CProperties> extensionsProperties;
	
	public SshServer(CServer cServer, List<CBean> beans) {
		super(cServer, beans);
	}
	
	@Override
	protected boolean parseProperties() {
		// parse the server properties
		for (CProperty property : this.server.getProperty()) {
			switch (property.getName()) {
			case "port":
				try {
					this.port = Integer.parseInt(property.getValue());
				} catch (NumberFormatException e) {
					LaunchingMessageKind.ESERVER0005.format(this, "invalid port value", property.getValue());
					return false;
				}
				break;
			default:
				break;
			}
		}
		this.extensionsProperties = this.server.getProperties();
		return true;
	}
	
	
	@Override
	public void start() {
		
		if(parseProperties()) {
			LaunchingMessageKind.ISERVER0001.format(this, this.port);
			this.sshd = org.apache.sshd.SshServer.setUpDefaultServer();
			this.sshd.setPort(this.port);
			this.sshd.setShellFactory(new SshShellFactory());
			
			List<NamedFactory<UserAuth>> userAuthFactories = new ArrayList<NamedFactory<UserAuth>>();
			userAuthFactories.add(new UserAuthPublicKey.Factory());
			this.sshd.setUserAuthFactories(userAuthFactories);

			for (CProperties properties: this.extensionsProperties) {
				processExtension(this.sshd, properties);
			}
			
			try {
				this.sshd.start();
				this.started=true;
				LaunchingMessageKind.ISERVER0004.format(this,"port: "+this.port);
			} catch (Exception e) {
				// Do nothing
				LaunchingMessageKind.ESERVER0002.format(this,e);
			}
			
		} else {
			LaunchingMessageKind.ESERVER0005.format(this,"");
		}
	}
	
	private void processExtension(org.apache.sshd.SshServer sshd2,
			CProperties properties) {
		// parse others set of properties => Authentification mechanism implementations
		
		//Instanciate the Properties Class
		String authClassName = properties.getValue();
		try {
			Class<?> authClass = Class.forName(authClassName);
			
			if(!Arrays.asList(authClass.getInterfaces()).contains(SshAuthExtension.class)) {
				LaunchingMessageKind.EEXT004.format(authClassName, this);
				return;
			}
			
			Constructor<?> constructor = authClass.getConstructor(List.class);
			ServerExtension extension = (ServerExtension)constructor.newInstance(properties.getProperty());
			extension.parseProperties(properties.getProperty());
			extension.process(this.sshd);
		} catch (ClassNotFoundException
				|NoSuchMethodException
				|SecurityException
				|InstantiationException
				|IllegalAccessException
				|IllegalArgumentException
				|InvocationTargetException
				|ExtensionException e) {
			LaunchingMessageKind.EEXT002.format(authClassName, e);
		}
	}

	@Override
	public void stop() {
		if (this.started) {
			LaunchingMessageKind.ISERVER0004.format(this, "Port:"+this.port);
			try {
				sshd.stop();
			} catch (InterruptedException e) {
				LaunchingMessageKind.ESERVER0004.format(this, e);
			}
		}
	}

	public boolean isStarted() {
		return started;
	}
	
}
