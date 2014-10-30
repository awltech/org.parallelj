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
package org.parallelj;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.parallelj.mirror.HandlerLoopPolicy;

/**
 * Specifies that the method is an exception handler. 
 * 
 * An handler is a special procedure which is called when an exception is thrown
 * by other procedures.
 * 
 * @author Atos Worldline
 * @since 0.3.0
 *
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Handler {

	/**
	 * The list of procedures to handle exception.
	 */
	String[] value() default {};

	/**
	 * The handler loop policy.
	 * Default value is RESUME.
	 * 
	 * @return The handler loop policy.
	 */
	HandlerLoopPolicy handlerLoopPolicy() default HandlerLoopPolicy.RESUME;
	
}
