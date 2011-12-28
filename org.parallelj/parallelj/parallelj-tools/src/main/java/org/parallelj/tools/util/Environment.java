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
package org.parallelj.tools.util;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;

/**
 * Helper class that holds the APT environment in a thread local.
 * 
 * The environment should be set/unset in
 * {@link AbstractProcessor#process(java.util.Set, javax.annotation.processing.RoundEnvironment)}
 * .
 * 
 * @author Laurent Legrand
 * 
 */
public class Environment {

	private static ThreadLocal<ProcessingEnvironment> environment = new ThreadLocal<ProcessingEnvironment>();

	/**
	 * Return the APT environment.
	 * 
	 * @return the APT environment or <code>null</code> if none have been set.
	 */
	public static ProcessingEnvironment getEnvironment() {
		return environment.get();
	}

	public static void setEnvironment(ProcessingEnvironment env) {
		environment.set(env);
	}

}