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
package org.parallelj.launching.internal.spi;

/**
 * Utilitary class that acts as a ServiceLoader identifier, using the Class and
 * the ClassLoader used for its instanciation
 * 
 * @author mvanbesien
 * @since 1.4.2
 * 
 * @param <E>
 */
public class ServiceLoaderIdentifier<E> {

	/**
	 * Class identifier
	 */
	private Class<E> clazz;

	/**
	 * ClassLoader identifier
	 */
	private ClassLoader classloader;

	/**
	 * Creates a ServiceLoader identifier
	 * 
	 * @param clazz
	 * @param classloader
	 */
	public ServiceLoaderIdentifier(Class<E> clazz, ClassLoader classloader) {
		this.clazz = clazz;
		this.classloader = classloader;
	}

	/**
	 * @return class identifier
	 */
	public Class<E> getClazz() {
		return clazz;
	}

	/**
	 * @return classLoader identifier
	 */
	public ClassLoader getClassloader() {
		return classloader;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof ServiceLoaderIdentifier<?>))
			return false;
		ServiceLoaderIdentifier<?> identifier = (ServiceLoaderIdentifier<?>) obj;
		return (this.clazz == null ? identifier.clazz == null : this.clazz.equals(identifier.clazz))
				&& (this.classloader == null ? identifier.classloader == null : this.classloader
						.equals(identifier.classloader));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (this.clazz != null ? this.clazz.hashCode() : 0);
		result = prime * result + (this.classloader != null ? this.classloader.hashCode() : 0);
		return result;
	}

}
