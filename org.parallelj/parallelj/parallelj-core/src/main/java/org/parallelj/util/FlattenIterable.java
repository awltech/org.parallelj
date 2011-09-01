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

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * {@link FlattenIterable} is the inverse of the {@link BatchIterable}: it
 * converts an {@link Iterable} of {@link Iterable iterables} of elements into
 * an {@link Iterable} of elements. For instance, it can be used to loop on a
 * collection of collections of elements wih one for each instruction.
 * 
 * Note. Flatten comes from OCL spec.
 * 
 * @author Atos Worldline
 * 
 * @param <E>
 */
public class FlattenIterable<E> implements Iterable<E> {

	class IteratorImpl implements Iterator<E> {

		Iterator<? extends Iterable<E>> iterator = FlattenIterable.this.iterable
				.iterator();

		Iterator<E> current;

		public boolean hasNext() {
			if ((this.current == null) || (!this.current.hasNext())) {
				this.current = this.iterator.hasNext() ? this.iterator.next()
						.iterator() : null;
			}
			return (this.current != null) && (this.current.hasNext());
		}

		public E next() {
			if (!this.hasNext()) {
				throw new NoSuchElementException();
			}
			return this.current.next();
		}

		public void remove() {
			throw new UnsupportedOperationException();
		}

	}

	private Iterable<? extends Iterable<E>> iterable;

	public FlattenIterable(Iterable<? extends Iterable<E>> iterable) {
		if (iterable == null) {
			throw new IllegalArgumentException("iterable must be != null");
		}
		this.iterable = iterable;
	}

	public Iterator<E> iterator() {
		return new IteratorImpl();
	}

}
