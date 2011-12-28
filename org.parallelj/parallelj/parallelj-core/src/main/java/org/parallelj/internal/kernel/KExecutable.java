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

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 
 * 
 * @author Laurent Legrand
 * 
 */
public class KExecutable {

	/**
	 * Collection of attributes
	 */
	Set<KAttribute> attributes = new HashSet<KAttribute>();

	/**
	 * Add an attribute
	 * 
	 * @param attribute
	 *            the attribute to add
	 */
	public void addAttribute(KAttribute attribute) {
		this.attributes.add(attribute);
		attribute.executable = this;
	}

	/**
	 * Get the attribute values
	 * 
	 * @param context
	 *            the context
	 * @return a map of attribute name/value. Empty map if no attributes/
	 */
	public Map<String, String> values(Object context) {

		if (this.attributes.isEmpty()) {
			return Collections.emptyMap();
		}
		Map<String, String> map = new HashMap<String, String>();
		for (KAttribute attribute : this.attributes) {
			String value = attribute.value(context);
			if (value != null) {
				map.put(attribute.getName(), value);
			}
		}
		return map;
	}

}
