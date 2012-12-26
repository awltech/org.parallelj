package org.parallelj.ssh.publickey;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.DSAPublicKeySpec;
import java.security.spec.RSAPublicKeySpec;

import org.apache.sshd.server.PublickeyAuthenticator;
import org.apache.sshd.server.session.ServerSession;
import org.bouncycastle.util.encoders.Base64;

public class URLPublicKeyAuthentificator implements PublickeyAuthenticator {
	private byte[] bytes;
	private int pos;
	private String authorizedKeysFile;

	public URLPublicKeyAuthentificator(String authorizedKeysFile) {
		this.authorizedKeysFile = authorizedKeysFile;
	}

	@Override
	public boolean authenticate(String username, PublicKey key,
			ServerSession session) {
		String strLine;
		boolean isOk = false;
		try {
			// Read Public Key.
			File filePublicKey = new File(this.authorizedKeysFile);
			FileReader fr = new FileReader(filePublicKey);
			BufferedReader br = new BufferedReader(fr);

			while ((strLine = br.readLine()) != null && !isOk) {
				PublicKey pKey = decodePublicKey(strLine);

				byte[] keyBytes = key.getEncoded();
				byte[] pKeyBytes = pKey.getEncoded();

				if (keyBytes == null || pKeyBytes == null
						|| keyBytes.length != pKeyBytes.length) {
					isOk = false;
					continue;
				}

				isOk = true;
				for (int i = 0; i < keyBytes.length; i++) {
					if (keyBytes[i] != pKeyBytes[i]) {
						isOk = false;
						continue;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			isOk = false;
		}
		return isOk;
	}

	private String decodeType() {
		int len = decodeInt();
		String type = new String(bytes, pos, len);
		pos += len;
		return type;
	}

	public PublicKey decodePublicKey(String keyLine) throws Exception {
		bytes = null;
		pos = 0;

		// look for the Base64 encoded part of the line to decode
		// both ssh-rsa and ssh-dss begin with "AAAA" due to the length bytes
		for (String part : keyLine.split(" ")) {
			if (part.startsWith("AAAA")) {
				byte[] bytePart = part.getBytes();
				bytes = Base64.decode(bytePart);
				break;
			}
		}
		if (bytes == null) {
			throw new IllegalArgumentException("no Base64 part to decode");
		}

		String type = decodeType();
		if (type.equals("ssh-rsa")) {
			BigInteger e = decodeBigInt();
			BigInteger m = decodeBigInt();
			RSAPublicKeySpec spec = new RSAPublicKeySpec(m, e);
			return KeyFactory.getInstance("RSA").generatePublic(spec);
		} else if (type.equals("ssh-dss")) {
			BigInteger p = decodeBigInt();
			BigInteger q = decodeBigInt();
			BigInteger g = decodeBigInt();
			BigInteger y = decodeBigInt();
			DSAPublicKeySpec spec = new DSAPublicKeySpec(y, p, q, g);
			return KeyFactory.getInstance("DSA").generatePublic(spec);
		} else {
			throw new IllegalArgumentException("unknown type " + type);
		}
	}

	private BigInteger decodeBigInt() {
		int len = decodeInt();
		byte[] bigIntBytes = new byte[len];
		System.arraycopy(bytes, pos, bigIntBytes, 0, len);
		pos += len;
		return new BigInteger(bigIntBytes);
	}

	private int decodeInt() {
		return ((bytes[pos++] & 0xFF) << 24) | ((bytes[pos++] & 0xFF) << 16)
				| ((bytes[pos++] & 0xFF) << 8) | (bytes[pos++] & 0xFF);
	}

}