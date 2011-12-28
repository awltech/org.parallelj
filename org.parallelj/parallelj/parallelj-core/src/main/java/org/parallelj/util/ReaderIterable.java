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
package org.parallelj.util;

import java.io.IOException;
import java.io.LineNumberReader;
import java.io.Reader;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * 
 * 
 * @author Atos Worldline
 *
 */
public class ReaderIterable implements Iterable<String> {

	class IteratorImpl implements Iterator<String> {

		boolean eof = false;

		String current;

		public boolean hasNext() {
			if (!this.eof && this.current == null) {
				try {
					this.current = ReaderIterable.this.reader.readLine();
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
				this.eof = this.current == null;
			}
			return !this.eof;
		}

		public String next() {
			if (!this.hasNext()) {
				throw new NoSuchElementException();
			}
			String s = this.current;
			this.current = null;
			return s;
		}

		public void remove() {
			throw new UnsupportedOperationException();
		}

	}

	LineNumberReader reader;

	public ReaderIterable(Reader reader) {
		this.reader = new LineNumberReader(reader);
	}

	public Iterator<String> iterator() {
		return new IteratorImpl();
	}

}
