package org.parallelj.ssh.publickey;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class IO {
	
	private IO() {
	}

	public static byte[] readAsBytes(InputStream in) throws IOException {
		return read(in).toByteArray();
	}

	public static void copy(InputStream in, OutputStream out)
			throws IOException {
		if (in == null) {
			throw new NullPointerException();
		}
		try {
			byte[] buffer = new byte[256];
			for (int l = in.read(buffer); l != -1; l = in.read(buffer)) {
				out.write(buffer, 0, l);
			}
		} finally {
			close(in);
		}
	}

	private static ByteArrayOutputStream read(InputStream in)
			throws IOException {
		if (in == null) {
			throw new NullPointerException();
		}
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		copy(in, baos);
		return baos;
	}

	public static void close(Closeable closeable) {
		if (closeable != null) {
			try {
				closeable.close();
			} catch (IOException ignore) {
			}
		}
	}
}
