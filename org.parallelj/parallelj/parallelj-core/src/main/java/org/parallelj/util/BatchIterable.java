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
 * {@link BatchIterable} converts an {@link Iterable} of elements into an
 * {@link Iterable} of {@link Iterable} of elements.
 * 
 * It allows to process elements by batch of elements instead of element by
 * element.
 * 
 * @author Atos Worldline
 * 
 * @param <E>
 */
public class BatchIterable<E> implements Iterable<Iterable<E>> {

	/**
	 * The default batch size
	 */
	public static final int DEFAULT_SIZE = 32;

	class IteratorImpl implements Iterator<Iterable<E>> {

		Iterator<E> iterator = BatchIterable.this.iterable.iterator();

		public boolean hasNext() {
			return this.iterator.hasNext();
		}

		public Iterable<E> next() {
			if (!this.hasNext()) {
				throw new NoSuchElementException();
			}
			List<E> list = new ArrayList<E>();
			for (int i = 0; i < BatchIterable.this.size; i++) {
				if (this.iterator.hasNext()) {
					list.add(this.iterator.next());
				} else {
					break;
				}
			}
			return list;
		}

		public void remove() {
			throw new UnsupportedOperationException();
		}

	}

	/**
	 * The iterable to convert.
	 */
	private Iterable<E> iterable;

	/**
	 * The batch size.
	 */
	private int size;

	/**
	 * Create a {@link BatchIterable} with a default batch size.
	 * 
	 * @param iterable
	 * @see #DEFAULT_SIZE
	 * @see BatchIterable#DemuxIterable(Iterable, int)
	 */
	public BatchIterable(Iterable<E> iterable) {
		this(iterable, DEFAULT_SIZE);
	}

	/**
	 * Create a {@link BatchIterable}
	 * 
	 * @param iterable
	 *            the iterable to convert
	 * @param size
	 *            the batch size
	 * 
	 * @throws IllegalArgumentException
	 *             if iterable is <code>null</code> or if size is <= 0
	 */
	public BatchIterable(Iterable<E> iterable, int size) {
		if (iterable == null) {
			throw new IllegalArgumentException("iterable must be != null");
		}
		if (size <= 0) {
			throw new IllegalArgumentException("size must be > 0");
		}
		this.iterable = new BufferedIterable<E>(iterable, 2 * size);
		this.size = size;
	}

	@Override
	public Iterator<Iterable<E>> iterator() {
		return new IteratorImpl();
	}

}
