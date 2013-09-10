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
package org.parallelj.internal.reflect.callback;

import java.lang.reflect.Field;
import java.util.Iterator;

import org.parallelj.internal.kernel.KProcess;
import org.parallelj.internal.kernel.callback.Iterable;
import org.parallelj.internal.reflect.ProgramAdapter;

public class FieldIterable implements Iterable {

	Field field;

	String name;

	public FieldIterable(Field field) {
		this.field = field;
		this.name = field.getName();
	}

	@Override
	public Iterator<?> iterator(KProcess process) {
		Iterator<?> iterator = ProgramAdapter
				.getIterators(process.getContext()).get(this);
		if (iterator == null) {
			// try new one
			java.lang.Iterable<?> iterable = null;
			try {
				iterable = (java.lang.Iterable<?>) this.field.get(process
						.getContext());
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (iterable != null) {
				iterator = iterable.iterator();
				if (iterator != null) {
					ProgramAdapter.getIterators(process.getContext()).put(
							this, iterator);
				}
			}
		}
		return iterator;
	}

	@Override
	public void close(KProcess process) {
		ProgramAdapter.getIterators(process.getContext()).remove(this);
	}
	
	/**
	 * This will return Iterable to Calling method as parameter. 
	 * @param process
	 * @return
	 */
	public java.lang.Iterable<?> getIterable(KProcess process) {
			// try new one
			java.lang.Iterable<?> iterable = null;
			try {
				iterable = (java.lang.Iterable<?>) this.field.get(process
						.getContext());
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		return iterable;
	}
}
