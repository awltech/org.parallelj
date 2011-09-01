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
