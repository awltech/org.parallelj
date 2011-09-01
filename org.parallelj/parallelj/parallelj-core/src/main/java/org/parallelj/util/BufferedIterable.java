/*
 *     ParallelJ, framework for parallel computing
 *
 *     Copyright (C) 2010 Atos Worldline or third-party contributors as
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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * 
 * 
 * @author Atos Worldline
 *
 * @param <E>
 */
public class BufferedIterable<E> implements Iterable<E> {
	
	public static final int DEFAULT_SIZE = 32;

	class IteratorImpl implements Iterator<E> {

		Iterator<E> iterator = BufferedIterable.this.iterable.iterator();

		List<E> buffer = new ArrayList<E>();

		public boolean hasNext() {
			if (this.buffer.isEmpty()) {
				this.fill();
			}
			return !this.buffer.isEmpty();
		}

		public E next() {
			if (!this.hasNext()) {
				throw new NoSuchElementException();
			}
			return this.buffer.remove(0);
		}

		public void remove() {
			throw new UnsupportedOperationException();
		}

		private void fill() {
			for (int i = 0; i < BufferedIterable.this.size; i++) {
				if (this.iterator.hasNext()) {
					this.buffer.add(this.iterator.next());
				} else {
					break;
				}
			}
		}
	}

	private Iterable<E> iterable;

	private int size;

	public BufferedIterable(Iterable<E> iterable) {
		this(iterable, DEFAULT_SIZE);
	}

	public BufferedIterable(Iterable<E> iterable, int size) {
		if (iterable == null) {
			throw new IllegalArgumentException("iterable must be != null");
		}
		if (size <= 0) {
			throw new IllegalArgumentException("size must be > 0");
		}
		this.iterable = iterable;
		this.size = size;
	}

	public Iterator<E> iterator() {
		return new IteratorImpl();
	}

}
