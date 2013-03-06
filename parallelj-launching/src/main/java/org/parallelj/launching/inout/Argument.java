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

import java.lang.reflect.Method;

import org.parallelj.launching.LaunchingMessageKind;
import org.parallelj.launching.parser.Parser;

/**
 * Represents a Program argument for a remote launching. 
 *
 */
public class Argument extends InOut {
	private Method writeMethod;

	/**
	 * The parser type for the field declared in the Program class 
	 */
	private Class<? extends Parser> parser;

	private Parser parserInstance;

	public Argument(final String name, final Class<?> type, final Class<? extends Parser> parserClass, final Method writeMethod) {
		this(name,type,parserClass, writeMethod, null);
	}

	/**
	 * Default constructor
	 * 
	 * @param name
	 * @param type
	 * @param parserClass
	 */
	public Argument(final String name, final Class<?> type, final Class<? extends Parser> parserClass, final Method writeMethod, final Object value) {
		this.name = name;
		this.type = type;
		this.parser = parserClass;
		this.value = value;
		this.writeMethod = writeMethod;
		
		instanciateParser();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public final String toString() {
		return "Argument=>name["+this.name+"]_type["+this.type+"]_parser:["+this.parser+"]_value:["+this.value+"]";
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

	public Method getWriteMethod() {
		return writeMethod;
	}

	public void setValueUsingParser(String value) {
		if (this.parserInstance==null) {
			instanciateParser();
		}
		this.value = this.parserInstance.parse(value);
	}

	private void instanciateParser() {
		if (this.parser!=null) {
			try {
				this.parserInstance = this.parser.newInstance();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				LaunchingMessageKind.EREMOTE0007.format(
						this.parser.getCanonicalName(), e);
			}
		}
	}
	
}

