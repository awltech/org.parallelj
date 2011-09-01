package org.parallelj.internal.kernel.callback;

/**
 * A callback used to read / write a property
 * 
 * @author Laurent Legrand
 * 
 * @param <E>
 *            the type of object
 * @since 0.4.0
 */
public interface Property<E> {

	/**
	 * Read the value from a given context
	 * 
	 * @param context
	 *            the context
	 * @return the value
	 */
	public E get(Object context);

	/**
	 * Set the value to a given context
	 * 
	 * @param context
	 *            the context
	 * @param value
	 *            the value
	 */
	public void set(Object context, E value);

}
