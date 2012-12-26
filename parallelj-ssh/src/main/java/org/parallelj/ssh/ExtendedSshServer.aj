package org.parallelj.ssh;
	
import java.util.List;

import org.parallelj.internal.conf.ConfigurationService;
import org.parallelj.internal.conf.ParalleljConfigurationManager;
import org.parallelj.internal.conf.pojos.ParalleljConfiguration;
import org.parallelj.launching.LaunchingMessageKind;
import org.parallelj.launching.internal.ext.Extension;
import org.parallelj.launching.internal.ext.ExtensionException;
import org.parallelj.launching.internal.ext.ExtensionService;

/**
 * 
 * Aspect that allow to customize the ParallelJ SSh server.
 * This allows to define different Authentification methods for ssh.
 *
 */
public privileged aspect ExtendedSshServer {
	
	public static final String SSH_TYPE="SSH";
	
	void around(Object context, org.apache.sshd.SshServer sshd): 
		execution(private void org.parallelj.launching.transport.ssh.SshServer.initialize(..)) 
		&& this(context) 
		&& args(sshd){
		
		// This extension must be activated only if ssh is configured (remote launching)
		ParalleljConfiguration conf = (ParalleljConfiguration)ConfigurationService
										.getConfigurationService()
										.getConfigurationManager()
										.get(ParalleljConfigurationManager.class)
										.getConfiguration();
		
		if (conf.getServers() != null 
				&& conf.getServers().getSsh()!=null
				&& conf.getServers().getSsh().getAuths()!=null
				&& conf.getServers().getSsh().getAuths().getAuth()!=null
				&& conf.getServers().getSsh().getAuths().getAuth().size()>0
				) {
			// Get all defined Exts
			List<Extension> extensions = ExtensionService.getExtensionService().getExtentionsByType(SSH_TYPE);
			for (Extension ext : extensions) {
				if ( ((SshExtension)ext).isInitialized() ) {
					try {
						((SshExtension)ext).process(sshd);
					} catch (ExtensionException e) {
						LaunchingMessageKind.EEXT003.format(ext.getClass().getCanonicalName(), e);
					}
				}
			}
		}
	}
	
}
