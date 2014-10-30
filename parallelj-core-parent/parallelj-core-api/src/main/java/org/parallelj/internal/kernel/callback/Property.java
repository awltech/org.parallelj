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
package org.parallelj.internal.kernel.callback;

/**
 * A callback used to read / write a property
 * 
 * @author Laurent Legrand
 * 
 * @param <E>
 *            the type of object
 * @since 0.4.0
 */
public interface Property<E> {

	/**
	 * Read the value from a given context
	 * 
	 * @param context
	 *            the context
	 * @return the value
	 */
	public E get(Object context);

	/**
	 * Set the value to a given context
	 * 
	 * @param context
	 *            the context
	 * @param value
	 *            the value
	 */
	public void set(Object context, E value);

}
