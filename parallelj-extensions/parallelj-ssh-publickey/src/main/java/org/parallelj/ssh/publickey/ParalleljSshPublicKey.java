package org.parallelj.ssh.publickey;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.sshd.SshServer;
import org.apache.sshd.common.NamedFactory;
import org.apache.sshd.server.UserAuth;
import org.apache.sshd.server.auth.UserAuthPublicKey;
import org.parallelj.internal.conf.ConfigurationService;
import org.parallelj.internal.conf.ParalleljConfigurationManager;
import org.parallelj.internal.conf.pojos.CAuth;
import org.parallelj.internal.conf.pojos.CProperty;
import org.parallelj.internal.conf.pojos.ParalleljConfiguration;
import org.parallelj.launching.internal.ext.AbstractExtension;
import org.parallelj.launching.internal.ext.ExtensionException;
import org.parallelj.ssh.ExtendedSshServer;
import org.parallelj.ssh.SshExtension;

public class ParalleljSshPublicKey extends AbstractExtension implements SshExtension {
	
	private final static String DEFAULT_SERVER_PRIVATE_KEY_RESOURCE = "/id_rsa"; 
	
	private boolean isInitialized = false;
	
	private URL privateServerKey;
	private String authorizedServerKey;
	
	private Map<String,String> props = new HashMap<String, String>();
	
	protected URL getServerPrivateKey(Map<String, String> props) {
		String propServerPrivateKey = props.get("server-private-key");
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
			
			try {
				urlServerPrivateKey = ParalleljSshPublicKey.class.getResource(DEFAULT_SERVER_PRIVATE_KEY_RESOURCE).toURI().toURL();
			} catch (MalformedURLException e) {
				// Do nothing
				ExtensionSshMessageKind.ESH0002.format();
			} catch (URISyntaxException e) {
				// Do nothing
				ExtensionSshMessageKind.ESH0002.format();
			}
		}
		return urlServerPrivateKey;
	}
			
	protected String getServerAuthorizedKey(Map<String, String> props) {
		String propAuthorizedKeysFileName = props.get("server-authorized-keys");
		// Try to load the server authorized public keys
		File serverAuthorizedKeyFile = new File(propAuthorizedKeysFileName);
		if (serverAuthorizedKeyFile.exists()) {
			return propAuthorizedKeysFileName;
		}
		ExtensionSshMessageKind.ESH0003.format(propAuthorizedKeysFileName);
		return null;
	}
	
	@Override
	public void init() throws ExtensionException {
		// Read Props for ssh...
		ParalleljConfiguration paralleljConf = (ParalleljConfiguration) ConfigurationService
			.getConfigurationService().getConfigurationManager()
			.get(ParalleljConfigurationManager.class).getConfiguration();
		List<CAuth> auths = paralleljConf.getServers().getSsh().getAuths().getAuth();
		for (CAuth cAuth : auths) {
			if (cAuth.getType().equalsIgnoreCase(ParalleljSshPublicKey.class.getCanonicalName())) {
				for(CProperty propety:cAuth.getProperty()) {
					this.props.put(propety.getName(), propety.getValue());  
				}
			}
		}
		
		try {
			this.authorizedServerKey = getServerAuthorizedKey(this.props);
		} catch (Exception e) {
			throw new ExtensionException();
		}
		
		this.privateServerKey = getServerPrivateKey(this.props);
		if(this.authorizedServerKey == null) {
			throw new ExtensionException();
		}
		
		this.isInitialized = true;
	}

	/**
	 * Any others configurations for the sshd server can be done directly with the SshServer object from Apache mina SSHD
	 * 
	 * Warnings:
	 * - The shell factory mustn't be changed if you want to be able to launch your Programs remotly...!
	 * - Changing the port of the Apache mina SSHD instance will not have any effect: sshd.setPort(port)       
	 *   The port of the ssh server is read from the parallelj.xml file
	 */
	@Override
	public void process(SshServer sshd)  throws ExtensionException {
		sshd.setKeyPairProvider(new URLKeyPairProvider(this.privateServerKey));

		List<NamedFactory<UserAuth>> userAuthFactories = new ArrayList<NamedFactory<UserAuth>>();
		userAuthFactories.add(new UserAuthPublicKey.Factory());
		sshd.setUserAuthFactories(userAuthFactories);

		sshd.setPublickeyAuthenticator(new URLPublicKeyAuthentificator(
				this.authorizedServerKey));
	}
	
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

	@Override
	public String getType() {
		return ExtendedSshServer.SSH_TYPE;
	}

	@Override
	public void destroy() {
		
	}
	
	@Override
	public Map<String, String> getProps() {
		return this.props;
	}

	@Override
	public boolean isInitialized() {
		return this.isInitialized;
	}

	public URL getPrivateServerKey() {
		return privateServerKey;
	}

	public String getAuthorizedServerKey() {
		return authorizedServerKey;
	}

	
}
