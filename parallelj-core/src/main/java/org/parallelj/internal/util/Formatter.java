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
package org.parallelj.internal.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.HashMap;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * Helper class used to format a log message
 * 
 * It will get a log4j logger based on the package name of the enumeration.
 * 
 * Default messages can be overridden by resource bundles. The qualified name of
 * the enumeration will be used to get the resource bundle.
 * 
 * @author Atos Worldline
 * @since 0.4.0
 * 
 * @param <E>
 *            the enumeration containing the list of message types
 */
public class Formatter<E extends Enum<E>> {

	/**
	 * Annotation containing the default template used to format a message.
	 * 
	 * Has to be used on a enumeration literal.
	 * 
	 * @author Atos Worldline
	 * 
	 */
	@Target(ElementType.FIELD)
	@Retention(RetentionPolicy.RUNTIME)
	public @interface Format {

		/**
		 * The template.
		 * 
		 * @return the template.
		 */
		String value();
	}

	/**
	 * The underlying logger
	 */
	Logger logger;

	/**
	 * The message templates retrieved from resource bundle
	 */
	private Map<E, String> templates = new HashMap<E, String>();

	public Formatter(Class<E> type) {

		// the logger name is the package name if exists
		String name = type.getName();
		int idx = name.lastIndexOf('.');
		if (idx != -1) {
			name = name.substring(0, idx);
		}
		this.logger = Logger.getLogger(name);

		try {
			for (E constant : type.getEnumConstants()) {
				Format format = type.getField(constant.name()).getAnnotation(
						Format.class);
				if (format != null) {
					templates.put(constant, constant + "\t" + format.value());
				}
			}
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// override default
		try {
			// get bundle corresponding to the class
			ResourceBundle bundle = ResourceBundle.getBundle(type.getName());
			// this.logger.info("override default templates with: " +
			// bundle.getLocale());
			for (E constant : type.getEnumConstants()) {
				if (!bundle.containsKey(constant.name())) {
					continue;
				}
				try {
					// this.logger.info("override template of: " +
					// constant.name());
					this.templates
							.put(constant,
									constant + "\t"
											+ bundle.getString(constant.name()));
				} catch (MissingResourceException e) {
					// ignore
				}
			}
		} catch (MissingResourceException e) {
			// ignore
		}
	}

	public String getFormatedMessage(E kind, Object... args) {
		return this.format(kind, args);
	}
	
	public String print(E kind, Object... args) {
		String message = this.format(kind, args);
		Level level = null;
		switch (kind.name().charAt(0)) {
		case 'E':
			level = Level.ERROR;
			break;
		case 'F':
			level = Level.FATAL;
			break;
		case 'I':
			level = Level.INFO;
			break;
		case 'W':
			level = Level.WARN;
			break;
		}

		// check if last arg is an exception
		if (args != null && args.length > 0
				&& args[args.length - 1] instanceof Throwable) {
			this.logger.log(level, message, (Throwable) args[args.length - 1]);
		} else {
			this.logger.log(level, message);
		}
		return message;

	}

	private String format(E kind, Object... args) {
		String format = this.templates.get(kind);
		if (format != null) {
			// the message format is enclosed by a try / catch block to avoid
			// throwing unexpected exception
			try {
				return String.format(format, args);
			} catch (RuntimeException e) {
				// ignore
			}
		}

		// default behavior: appending all args
		StringBuilder builder = new StringBuilder();
		builder.append(kind);
		boolean first = true;
		for (Object o : args) {
			builder.append((first) ? '\t' : ", ");
			first = false;
			builder.append(o);
		}

		return builder.toString();
	}

}
