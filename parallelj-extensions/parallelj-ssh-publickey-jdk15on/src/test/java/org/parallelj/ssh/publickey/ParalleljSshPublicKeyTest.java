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

import java.security.KeyPair;
import java.security.PublicKey;
import java.util.Iterator;

import org.apache.sshd.SshServer;
import org.apache.sshd.server.auth.UserAuthPublicKey;
import org.apache.sshd.common.keyprovider.AbstractKeyPairProvider;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.parallelj.ssh.ExtendedSshServer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;


public class ParalleljSshPublicKeyTest {
	
	private ParalleljSshPublicKey paralleljSshPublicKey;
	private SshServer sshdServer; 
	
	@Test
	public void SshPublicKeyExtensionTest()
		throws Exception {
		
		assertNotNull(this.sshdServer.getUserAuthFactories());
		assertEquals(this.sshdServer.getUserAuthFactories().size(), 1);
		assertTrue(this.sshdServer.getUserAuthFactories().get(0) instanceof UserAuthPublicKey.Factory);
		
	}
	
	@Test
	public void SshPublicKeyExtensionURLKeyPairProviderTest()
		throws Exception {
		
		assertNotNull(this.sshdServer.getKeyPairProvider());
		assertTrue(this.sshdServer.getKeyPairProvider() instanceof ParallelJURLKeyPairProvider);
		ParallelJURLKeyPairProvider urlKeyPairProvider = (ParallelJURLKeyPairProvider)this.sshdServer.getKeyPairProvider();
		
		KeyPair[] keyPairs = urlKeyPairProvider.loadKeysForTesting();
		assertNotNull(keyPairs);
		assertEquals(keyPairs.length, 1);
		
		assertNotNull(keyPairs[0].getPrivate());
		assertNotNull(keyPairs[0].getPublic());
	}	

	@Test
	public void URLPublicKeyAuthentificatorTest()
		throws Exception {
		
		assertNotNull(this.sshdServer.getPublickeyAuthenticator());
		assertTrue(this.sshdServer.getPublickeyAuthenticator() instanceof URLPublicKeyAuthentificator);
		
		URLPublicKeyAuthentificator urlAuthent = (URLPublicKeyAuthentificator)this.sshdServer.getPublickeyAuthenticator();
		// The public key available in authorized_keys
		PublicKey pubKey = urlAuthent.decodePublicKey("ssh-dss AAAAB3NzaC1kc3MAAACBAO1lLGFNsfIXbwC5AfHUshIj/74KS6VDyMTshRu6FLoRQQXayP+ZoHM5tC47iznRa/KFoIJJ/sXdV6rHfsywNAqh85WmdOhKDN2s7mejqR79mEMvNWX1j7/6YvoudkSU4CmT9nFttjoxbSMaTSbyrGYxut3a/GTARjoBGtfXbQjLAAAAFQCFSFypRzVAO2lqMIoJ0oTzLZvz7wAAAIAhk9M6QHOjE43H++XRVHqNRt7mkqpV/bnAGp1N3HQ1IemKRGRdHurA47/sSFnYwqU/t8+mX0Ypunm7lHxOlfIjNjVa8+vb0/b3gWihYJeSrY0CyctChPLRTydjsu2z2E6e82TmgaG2NdHsE0swhnKIQNKGIT9erD/uaUMYlFcSBgAAAIEAw5SfIYVEktzATol912k4cqOLNWTg3R0UCdJWrhgEzMM/Gs/I1yupKZt8Tz2rve5PCEkU3GLHM9XN92yZoGHcnpBxMs5WnWswkMvTDkLRMGwpJSU5P8V2nhrBt/VjZyvW3k8iYvzneXsOjzGyuM+QFLmhM5evWpsvMghWhbtTbyI= ");
		assertTrue(urlAuthent.authenticate(null, pubKey, null));
		// Another public key not available in authorized_keys
		pubKey = urlAuthent.decodePublicKey("ssh-rsa AAAAB3NzaC1yc2EAAAABIwAAAQEAwWJrPZ3j7Bgwx8I/BayLs2Cq26PRHJQXhtLsjAOzPMZ6/ZJuCEYEk7UXgQ7bT+KondYSNc6PKXxRHjEuomFo+KlKAZ4KIhYf7kAug3leiG9n2eWE3Vhk5Aad3H+hwz9G+zjV80uzYKV/gPIzq9qqsLUnL7Q6N9IgfdpwoaqTdLCRd3sWoSllhbWMP4oBiGm0M7KA9KIi/zOBwXBpyPx6lZZ3xkF+Sia1SzV8AwwIMv8czAUu/K1cE6tjecCxrQ9uSW2/lI0x+O6qqQyZS0j7rqHnmTWDXD9bvfM1gzMohk1Pn9bksjWCc1C4i0rngJHIEE1XwcnW0ydOo74n3hKSTQ== ");
		assertFalse(urlAuthent.authenticate(null, pubKey, null));
		
	}

	@Before
	public void setUp()
		throws Exception {
		this.paralleljSshPublicKey = new ParalleljSshPublicKey();
		this.sshdServer = new SshServer();
		
		assertEquals(this.paralleljSshPublicKey.getType(), ExtendedSshServer.SSH_TYPE);
		assertFalse(this.paralleljSshPublicKey.isInitialized());
		assertNotNull(this.paralleljSshPublicKey.getProps());
		
		this.paralleljSshPublicKey.init();
		assertTrue(this.paralleljSshPublicKey.isInitialized());
		
		assertTrue(this.paralleljSshPublicKey.getPrivateServerKey().getFile().endsWith("parallelj-ssh-publickey-jdk15on/src/test/resources/id_rsa"));
		assertEquals(this.paralleljSshPublicKey.getAuthorizedServerKey(), "src/test/resources/authorized_keys");
		
		this.paralleljSshPublicKey.process(this.sshdServer);
	}

	@After
	public void tearDown()
		throws Exception {
	}

}