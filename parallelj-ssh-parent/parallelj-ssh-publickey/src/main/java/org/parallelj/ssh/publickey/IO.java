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
