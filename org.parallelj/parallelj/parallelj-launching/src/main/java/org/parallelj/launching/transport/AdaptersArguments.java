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
package org.parallelj.launching.transport;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.parallelj.launching.In;
import org.parallelj.launching.LaunchingMessageKind;
import org.parallelj.launching.parser.Parser;
import org.parallelj.launching.transport.tcp.program.ArgEntry;

/**
 * Entry point for all defined arguments of Adapters for remote launching
 *
 */
public final class AdaptersArguments {
	
	/**
	 * Define the arguments for an Adapter
	 */
	public static final class AdapterArguments {
		/**
		 *  The canonical name of the Adapter type
		 */
		private String adapterClassName;
		
		/**
		 * Available Adapter arguments
		 */
		private List<ArgEntry> adapterArguments;

		/**
		 * Default constructor
		 * 
		 * @param adapterClassName
		 * @param adapterArguments
		 */
		private AdapterArguments(final String adapterClassName,
				final List<ArgEntry> adapterArguments) {
			this.adapterClassName = adapterClassName;
			this.adapterArguments = adapterArguments;
		}

		/**
		 * Getter for the adapterClassName 
		 * 
		 * @return String, the adapater Type 
		 */
		public String getAdapterClassName() {
			return this.adapterClassName;
		}

		/**
		 * Getter for the adapter arguments 
		 * 
		 * @return a List of arguments for the adapter
		 */
		public List<ArgEntry> getAdapterArguments() {
			return this.adapterArguments;
		}
	}
	
	/**
	 * List of all Adapters and its arguments
	 */
	private static List<AdapterArguments> adaptersArguments = new ArrayList<AdaptersArguments.AdapterArguments>();
	
	/**
	 * Default constructor
	 */
	private AdaptersArguments() {
	}

	/**
	 * Get the available arguments for an Adapter
	 * 
	 * @param clazz the Adapter Type as a String 
	 * @return a list of the Adapter arguments 
	 */
	public static List<ArgEntry> getAdapterArguments(final Class<?> clazz) {
		for (AdapterArguments adapterArgument:adaptersArguments) {
			if (adapterArgument.adapterClassName.equalsIgnoreCase(clazz.getCanonicalName())) {
				return adapterArgument.adapterArguments;
			}
		}
		return null;
	}
	
	/**
	 * Add a entry for an Adapter class in the defined arguments
	 * 
	 * @param clazzName the canonical name of the Program Class
	 */
	public static void addAdapter(final String clazzName) {
		Class<?> clazz;
		try {
			clazz = Class.forName(clazzName);
			final List<ArgEntry> adapterArgs = new ArrayList<ArgEntry>();

			// Search for annotation @In on attributes of
			// class clazz
			for (Field field : clazz.getDeclaredFields()) {
				// Search for an annotation @In
				for (Annotation annotation : field.getAnnotations()) {
					if (annotation.annotationType().equals(In.class)) {
						// Add the attribute where is the @In annotation and
						// the Parser class
						final Class<? extends Parser> parserClass = ((In)annotation).parser();
						adapterArgs.add(new ArgEntry(field.getName(), field.getType(), parserClass));
					}
				}
			}
			adaptersArguments.add(new AdapterArguments(clazzName, adapterArgs));
		} catch (ClassNotFoundException e) {
			LaunchingMessageKind.EREMOTE0001.format(clazzName, e);
		}
	}
	
	/**
	 * Get the number of defined Adapters (and its arguments) 
	 * 
	 * @return the number of defined Adapters
	 */
	public static int size() {
		return adaptersArguments.size();
	}
	
	/**
	 * Get the Adapter arguments at a specific position in the List
	 * 
	 * @param index the position in the List
	 * @return the AdaptersArguments
	 */
	public static AdapterArguments getAdapterArgument(final int index) {
		return adaptersArguments.get(index);
	}
}
