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
package org.parallelj.ssh.publickey;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

import org.apache.sshd.SshServer;
import org.parallelj.internal.conf.pojos.CProperty;
import org.parallelj.launching.LaunchingMessageKind;
import org.parallelj.launching.internal.ext.ExtensionException;
import org.parallelj.servers.ssh.SshAuthExtension;

public class ParalleljSshPublicPrivateKey implements SshAuthExtension {

	private final static String DEFAULT_SERVER_PRIVATE_KEY_RESOURCE = "/id_rsa"; 
	
	private URL privateServerKey;
	private String authorizedServerKey;
	
	List<CProperty> properties;

	public ParalleljSshPublicPrivateKey(List<CProperty> properties) {
		super();
		this.properties = properties;
	}

	@Override
	public boolean parseProperties(List<CProperty> properties) {
		for (CProperty property : this.properties) {
			switch (property.getName()) {
			case "server-authorized-keys":
				this.authorizedServerKey = getServerAuthorizedKey(property.getValue());
				if(this.authorizedServerKey == null) {
					LaunchingMessageKind.ESERVER0006.format(this, "server-authorized-key:"+property.getValue());
					return false;
				}
				break;
			case "server-private-key":
				try {
					this.privateServerKey = getServerPrivateKey(property.getValue());
				} catch (MalformedURLException|URISyntaxException e) {
					LaunchingMessageKind.ESERVER0006.format(this, "server-private-key:"+property.getValue());
				}
				break;

			default:
				break;
			}
			
		}
		return true;
	}

	@Override
	public void process(Object... parameters) throws ExtensionException {
		
		SshServer sshd = ((SshServer)parameters[0]);
		sshd.setKeyPairProvider(new URLKeyPairProvider(this.privateServerKey));

		sshd.setPublickeyAuthenticator(new URLPublicKeyAuthentificator(
				this.authorizedServerKey));
		
	}
	
	protected URL getServerPrivateKey(String propServerPrivateKey) throws MalformedURLException, URISyntaxException {
		// Try to load the defined one in the SSH configuration
		URL urlServerPrivateKey = null;
		try {
			urlServerPrivateKey = getUrlFromFile(propServerPrivateKey);
		} catch (Exception e) {
			// Do nothing: urlServerPrivateKey is null
		}
		// If 
		if (urlServerPrivateKey==null) {
			ExtensionSshMessageKind.WSH0001.format();
			urlServerPrivateKey = ParalleljSshPublicPrivateKey.class.getResource(DEFAULT_SERVER_PRIVATE_KEY_RESOURCE).toURI().toURL();
		}
		return urlServerPrivateKey;
	}
	
	
	protected String getServerAuthorizedKey(String propAuthorizedKeysFileName) {
		// Try to load the server authorized public keys
		File serverAuthorizedKeyFile = new File(propAuthorizedKeysFileName);
		if (serverAuthorizedKeyFile.exists()) {
			return propAuthorizedKeysFileName;
		}
		ExtensionSshMessageKind.ESH0003.format(propAuthorizedKeysFileName);
		return null;
	}

	/**
	 * Any others configurations for the sshd server can be done directly with the SshServer object from Apache mina SSHD
	 * 
	 * Warnings:
	 * - The shell factory mustn't be changed if you want to be able to launch your Programs remotly...!
	 * - Changing the port of the Apache mina SSHD instance will not have any effect: sshd.setPort(port)       
	 *   The port of the ssh server is read from the parallelj.xml file
	 */
	private URL getUrlFromFile(String fullFileName) {
		File file = new File(fullFileName);
		if (file.exists()) {
			try {
				return file.toURI().toURL();
			} catch (MalformedURLException e) {
				// Do nothing, the url is not valid..
			}
		}
		return null;
	}
}
