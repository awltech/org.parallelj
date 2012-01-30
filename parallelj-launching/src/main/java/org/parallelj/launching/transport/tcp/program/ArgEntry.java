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
package org.parallelj.launching.transport.tcp.program;

import org.parallelj.launching.parser.Parser;

/**
 * Represents a Program argument for a remote launching. 
 *
 */
public class ArgEntry {
	/**
	 * The name of the field declared in the Program class
	 */
	private String name;
	
	/**
	 * The type of the field declared in the Program class 
	 */
	private Class<?> type;
	
	/**
	 * The parser type for the field declared in the Program class 
	 */
	private Class<? extends Parser> parser;

	/**
	 * Default constructor
	 * 
	 * @param name
	 * @param type
	 * @param parserClass
	 */
	public ArgEntry(final String name, final Class<?> type, final Class<? extends Parser> parserClass) {
		this.name = name;
		this.type = type;
		this.parser = parserClass;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public final String toString() {
		return "ArgEntry=>name["+this.name+"]_type["+this.type+"]_parser:["+this.parser+"]";
	}

	/**
	 * Get the name of the Program field corresponding to this ArgEntry 
	 * 
	 * @return the name of the declared field in the Program class
	 */
	public final String getName() {
		return name;
	}

	/**
	 * Get the Type of the program field corresponding to this ArgEntry
	 * 
	 * @return the type of the declared field in the Program class
	 */
	public final Class<?> getType() {
		return type;
	}

	/**
	 * Get the Type of the Parser to use to convert a String to a complex Type.
	 * The String value comes from a remote launching. 
	 * 
	 * @return the type of the parser defined for the declared field in the Program class
	 */
	public final Class<? extends Parser> getParser() {
		return parser;
	}
}

