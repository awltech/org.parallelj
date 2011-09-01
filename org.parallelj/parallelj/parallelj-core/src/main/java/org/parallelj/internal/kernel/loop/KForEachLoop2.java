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

package org.parallelj.internal.kernel.loop;

import java.util.Iterator;

import org.parallelj.internal.kernel.KCall;
import org.parallelj.internal.kernel.KInputParameter;
import org.parallelj.internal.kernel.KProcedure;
import org.parallelj.internal.kernel.KProcess;
import org.parallelj.internal.kernel.callback.Iterable;
import org.parallelj.internal.kernel.callback.Predicate;

/**
 * 
 * 
 * @author Atos Worldline
 * 
 */
public class KForEachLoop2 extends KWhileLoop {

	/**
	 * 
	 */
	private Iterable iterable;

	/**
	 * KParameter that will store the element returned by the iterator.
	 */
	private KInputParameter element = new KInputParameter();

	/**
	 * Create a new for each loop.
	 * 
	 * @param first
	 *            the first procedure of the loop
	 * @param last
	 *            the last procedure of the loop
	 */
	public KForEachLoop2(final KProcedure first, final KProcedure last) {
		super(first, last);
		first.addInputParameter(this.element);
		super.setPredicate(new Predicate() {

			@Override
			public boolean verify(KProcess process) {
				return KForEachLoop2.this.hasNext(process);
			}
		});
	}

	@Override
	protected void iterating(KCall call) {
		this.element.set(call, this.iterable.iterator(call.getProcess()));
	}

	boolean hasNext(KProcess process) {
		Iterator<?> iterator = this.iterable.iterator(process);
		return (iterator == null) ? false : iterator.hasNext();
	}

	/**
	 * @return the iterable this loop iterate on
	 */
	public Iterable getIterable() {
		return iterable;
	}

	/**
	 * Set the iterable this loop iterate on
	 * 
	 * @param iterable
	 *            the iterable
	 */
	public void setIterable(Iterable iterable) {
		this.iterable = iterable;
	}

}
