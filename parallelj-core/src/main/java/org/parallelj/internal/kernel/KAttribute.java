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
package org.parallelj.internal.kernel;

import org.parallelj.Attribute;
import org.parallelj.internal.kernel.callback.Property;

/**
 * Represents an element annotated by {@link Attribute}.
 * 
 * @author Laurent Legrand
 * 
 */
public class KAttribute {

	/**
	 * The name of the attribute.
	 */
	String name;

	/**
	 * The executable that contains this attribute.
	 */
	KExecutable executable;

	/**
	 * The Java property used to read the attribute value.
	 */
	Property<String> property;

	/**
	 * @return the name of the attribute
	 */
	public String getName() {
		return name;
	}

	/**
	 * Set the name of the attribute
	 * 
	 * @param name
	 *            the new name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Return the value of the attribute
	 * 
	 * @param context
	 *            the context
	 * @return the value of the attribute. Might be <code>null</code>.
	 */
	public String value(Object context) {
		return this.property != null ? this.property.get(context) : null;
	}

	/**
	 * Get the Java property used to read the attribute value.
	 * 
	 * @return the Java property used to read the attribute value.
	 */
	public Property<String> getProperty() {
		return property;
	}

	/**
	 * Set the Java property used to read the attribute value.
	 * 
	 * @param property
	 *            the Java property used to read the attribute value.
	 */
	public void setProperty(Property<String> property) {
		this.property = property;
	}

}
