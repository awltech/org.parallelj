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
package org.parallelj.launching.transport.jmx;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

import org.parallelj.launching.internal.spi.CacheableServiceLoader;


/**
 * Entry point for all available options for Jmx remote launching
 *
 */
public final class JmxOptions {
	
	/**
	 * Available options for Jmx
	 */
	private final List<JmxOption> options = new ArrayList<JmxOption>();
	
	/**
	 * The instance of JmxOptions
	 */
	private static JmxOptions instance = new JmxOptions();

	/**
	 * Default constructor
	 */
	private JmxOptions() {
		// Search for available commands
		ServiceLoader<JmxOption> loader = CacheableServiceLoader.INSTANCE.load(JmxOption.class, JmxOptions.class.getClassLoader());
		if (loader==null || loader.iterator()==null || !loader.iterator().hasNext()) {
			loader = CacheableServiceLoader.INSTANCE.load(JmxOption.class, Thread.currentThread().getContextClassLoader());
		}
		for (JmxOption option:loader) {
			this.options.add(option);
		}
	}

	/**
	 * Get the available options
	 * 
	 * @return a List of all available JmxOptions
	 */
	public static List<JmxOption> getOptions() {
		return instance.options;
	}
}
