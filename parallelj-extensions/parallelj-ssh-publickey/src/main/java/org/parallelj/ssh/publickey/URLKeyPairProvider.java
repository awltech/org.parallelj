package org.parallelj.ssh.publickey;

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

public class URLKeyPairProvider extends AbstractKeyPairProvider {

	/** . */
	private final URL key;

	public URLKeyPairProvider(URL key) {
		this.key = key;
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
		return keys.toArray(new KeyPair[keys.size()]);
	}
}
