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

