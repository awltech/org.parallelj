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
package org.parallelj.internal.reflect;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

/**
 * The {@link ElementBuilderFactory} is a factory for {@link ElementBuilder}.
 * 
 * The {@link java.util.ServiceLoader} is used to retrieve the list of
 * {@link ElementBuilderFactory}.
 * 
 * @author Laurent Legrand
 * 
 */
public abstract class ElementBuilderFactory {

	private static ServiceLoader<ElementBuilderFactory> loader;
	
	static {
		loader = ServiceLoader.load(ElementBuilderFactory.class, ElementBuilderFactory.class.getClassLoader());
		if (loader==null || loader.iterator()==null || !loader.iterator().hasNext()) {
			loader = ServiceLoader.load(ElementBuilderFactory.class, Thread.currentThread().getContextClassLoader());
		}
	}

	/**
	 * The factory returns an {@link ElementBuilder} if an element has to be
	 * built from the class or <code>null</code> if no element needs to be
	 * built.
	 * 
	 * @param type
	 * @return
	 */
	public abstract ElementBuilder newBuilder(Class<?> type);

	/**
	 * Return an array of builders that will build one part of the program.
	 * 
	 * @param type
	 *            the type of the program.
	 * @return an array of {@link ElementBuilder} or an empty array if none is
	 *         found.
	 */
	public static ElementBuilder[] newBuilders(Class<?> type) {

		List<ElementBuilder> builders = new ArrayList<ElementBuilder>();

		for (ElementBuilderFactory factory : loader) {
			ElementBuilder b = factory.newBuilder(type);
			if (b != null) {
				builders.add(b);
			}
		}
		return builders.toArray(new ElementBuilder[0]);
	}
}