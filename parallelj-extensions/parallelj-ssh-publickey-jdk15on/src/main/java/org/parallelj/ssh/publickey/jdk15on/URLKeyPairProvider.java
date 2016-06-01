package org.parallelj.ssh.publickey.jdk15on;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.security.KeyPair;
import java.util.ArrayList;
import java.util.List;

import org.apache.sshd.common.keyprovider.AbstractKeyPairProvider;
import org.apache.sshd.common.util.SecurityUtils;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.parallelj.ssh.publickey.ExtensionSshMessageKind;
import org.parallelj.ssh.publickey.IO;
import org.parallelj.ssh.publickey.ParallelJURLKeyPairProvider;


public class URLKeyPairProvider extends AbstractKeyPairProvider implements ParallelJURLKeyPairProvider{

	/** . */
	private URL key;

	@Override
	public void setPrivateServerKey(URL url) {
		this.key = url;
	}

	@Override
	protected KeyPair[] loadKeys() {
		if (!SecurityUtils.isBouncyCastleRegistered()) {
			throw new IllegalStateException(
					"BouncyCastle must be registered as a JCE provider");
		}
		List<KeyPair> keys = new ArrayList<KeyPair>();
		if (key != null) {
			try {
				URLConnection conn = this.key.openConnection();
				byte[] content = IO.readAsBytes(conn.getInputStream());

				PEMParser r = new PEMParser(new InputStreamReader(
						new ByteArrayInputStream(content)));
				JcaPEMKeyConverter converter = new JcaPEMKeyConverter().setProvider("BC");	
				try {
					Object o = r.readObject();
					if (o instanceof PEMKeyPair) {
						KeyPair keypair = converter.getKeyPair((PEMKeyPair)o);
						keys.add(keypair);
					}
				} finally {
					r.close();
				}
			} catch (Exception e) {
				ExtensionSshMessageKind.ISH0001.format(key, e);
			}
		}
		return keys.toArray(new KeyPair[keys.size()]);
	}
	
	/**
	 * This method is written for satisfying the test case scenario. And as the
	 * Apache SSH libraries( org.apache.sshd / sshd-core / 0.6.0) do not have
	 * "Iterable<KeyPair> loadKeys()" this workaround is done.
	 */
	@Override
	public KeyPair[] loadKeysForTesting() {
		return loadKeys();
	}
}
