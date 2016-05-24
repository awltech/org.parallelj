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
package org.parallelj.ssh.publickey.jdk16;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.security.KeyPair;
import java.util.ArrayList;
import java.util.List;

import org.apache.sshd.common.keyprovider.AbstractKeyPairProvider;
import org.apache.sshd.common.util.SecurityUtils;
import org.bouncycastle.openssl.PEMReader;

import org.parallelj.ssh.publickey.ExtensionSshMessageKind;
import org.parallelj.ssh.publickey.IO;
import org.parallelj.ssh.publickey.ParallelJURLKeyPairProvider;

public class URLKeyPairProvider extends AbstractKeyPairProvider implements ParallelJURLKeyPairProvider {

	/** . */
	private URL key;

	@Override
	public void setPrivateServerKey(URL url) {
		this.key = url;
	}
	
	@Override
	public Iterable<KeyPair> loadKeys() {
		if (!SecurityUtils.isBouncyCastleRegistered()) {
			throw new IllegalStateException(
					"BouncyCastle must be registered as a JCE provider");
		}
		List<KeyPair> keys = new ArrayList<KeyPair>();
		if (key != null) {
			try {
				URLConnection conn = this.key.openConnection();
				byte[] content = IO.readAsBytes(conn.getInputStream());

				PEMReader r = new PEMReader(new InputStreamReader(
						new ByteArrayInputStream(content)));

				try {
					Object o = r.readObject();
					if (o instanceof KeyPair) {
						keys.add((KeyPair) o);
					}
				} finally {
					r.close();
				}
			} catch (Exception e) {
				ExtensionSshMessageKind.ISH0001.format(key, e);
			}
		}
		return keys;
	}
}
