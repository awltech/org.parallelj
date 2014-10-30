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
package org.parallelj.launching.inout;


/**
 * Represents a Program argument or output field for launching. 
 *
 */
public abstract class InOut {
	/**
	 * The name of the field declared in the Program class
	 */
	protected String name;
	
	protected int index;

	/**
	 * The type of the field declared in the Program class 
	 */
	protected Class<?> type;
	
	/**
	 * The value set for this field
	 */
	protected Object value;
	
	/**
	 * Get the name of the Program field corresponding to this InOut 
	 * 
	 * @return the name of the declared field in the Program class
	 */
	public final String getName() {
		return this.name;
	}

	/**
	 * Get the Type of the program field corresponding to this InOut
	 * 
	 * @return the type of the declared field in the Program class
	 */
	public final Class<?> getType() {
		return this.type;
	}

	public Object getValue() {
		return this.value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public int getindex() {
		return this.index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

}

