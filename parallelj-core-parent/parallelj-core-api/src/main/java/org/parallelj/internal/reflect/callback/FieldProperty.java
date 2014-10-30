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

import org.parallelj.internal.kernel.callback.Property;

/**
 * An implementation of {@link Property} based on {@link Field} reflection.
 * 
 * @author Laurent Legrand
 *
 * @param <E>
 * @since 0.4.0
 */
public class FieldProperty<E> implements Property<E> {

	Field field;

	public FieldProperty(Field field) {
		super();
		this.field = field;
	}

	@SuppressWarnings("unchecked")
	@Override
	public E get(Object context) {
		try {
			return (E) this.field.get(context);
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void set(Object context, E value) {
		// TODO Auto-generated method stub
	}

}
