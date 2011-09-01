package org.parallelj.internal.reflect.callback;

import java.lang.reflect.Field;

import org.parallelj.internal.kernel.callback.Property;

/**
 * An implementation of {@link Property} based on {@link Field} reflection.
 * 
 * @author Laurent Legrand
 *
 * @param <E>
 * @since 0.4.0
 */
public class FieldProperty<E> implements Property<E> {

	Field field;

	public FieldProperty(Field field) {
		super();
		this.field = field;
	}

	@SuppressWarnings("unchecked")
	@Override
	public E get(Object context) {
		try {
			return (E) this.field.get(context);
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void set(Object context, E value) {
		// TODO Auto-generated method stub
	}

}
