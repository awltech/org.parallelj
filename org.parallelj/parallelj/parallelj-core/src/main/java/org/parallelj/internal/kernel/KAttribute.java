package org.parallelj.internal.kernel;

import org.parallelj.Attribute;
import org.parallelj.internal.kernel.callback.Property;

/**
 * Represents an element annotated by {@link Attribute}.
 * 
 * @author Laurent Legrand
 * 
 */
public class KAttribute {

	/**
	 * The name of the attribute.
	 */
	String name;

	/**
	 * The executable that contains this attribute.
	 */
	KExecutable executable;

	/**
	 * The Java property used to read the attribute value.
	 */
	Property<String> property;

	/**
	 * @return the name of the attribute
	 */
	public String getName() {
		return name;
	}

	/**
	 * Set the name of the attribute
	 * 
	 * @param name
	 *            the new name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Return the value of the attribute
	 * 
	 * @param context
	 *            the context
	 * @return the value of the attribute. Might be <code>null</code>.
	 */
	public String value(Object context) {
		return this.property != null ? this.property.get(context) : null;
	}

	/**
	 * Get the Java property used to read the attribute value.
	 * 
	 * @return the Java property used to read the attribute value.
	 */
	public Property<String> getProperty() {
		return property;
	}

	/**
	 * Set the Java property used to read the attribute value.
	 * 
	 * @param property
	 *            the Java property used to read the attribute value.
	 */
	public void setProperty(Property<String> property) {
		this.property = property;
	}

}
