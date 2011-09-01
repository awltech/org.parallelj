package org.parallelj;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Map;

import org.aspectj.lang.Aspects;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.parallelj.internal.kernel.KAttribute;
import org.parallelj.internal.kernel.KExecutable;
import org.parallelj.internal.kernel.callback.Property;
import org.parallelj.internal.reflect.callback.FieldProperty;
import org.parallelj.internal.util.Classes;

/**
 * Helper class that return the values of the attributes of a class.
 * 
 * @author Laurent Legrand
 * @since 0.4.0
 */
public class Executables {

	static class StringProperty implements Property<String> {

		Property<?> property;

		/**
		 * @param property
		 */
		StringProperty(final Property<?> property) {
			super();
			this.property = property;
		}

		@Override
		public String get(Object context) {
			Object o = this.property.get(context);
			return (o == null) ? null : o.toString();
		}

		@Override
		public void set(Object context, String value) {
			// TODO Auto-generated method stub

		}

	}

	/**
	 * An aspect managing the {@link Attribute} of an {@link Executable}.
	 * 
	 * @author Laurent Legrand
	 */
	@Aspect("pertypewithin(@org.parallelj.Executable *)")
	public static class PerExecutable {

		KExecutable executable;

		/**
		 * Build the {@link KExecutable} corresponding to the class annotated
		 * with {@link Executable}.
		 * 
		 * @param joinPoint the AspectJ {@link JoinPoint}
		 */
		@SuppressWarnings("rawtypes")
		@After("staticinitialization(@org.parallelj.Executable *)")
		public void build(JoinPoint joinPoint) {
			Class<?> type = joinPoint.getSignature().getDeclaringType();
			this.executable = new KExecutable();

			for (Field field : Classes.findFields(type, Attribute.class)) {

				field.setAccessible(true);
				KAttribute attribute = new KAttribute();
				attribute.setName(field.getName());
				attribute.setProperty(new StringProperty(new FieldProperty(
						field)));
				this.executable.addAttribute(attribute);
			}

		}
	}

	static final Map<String, String> empty = Collections.emptyMap();

	/**
	 * Return the values of the attributes
	 * 
	 * @param executable
	 *            an instance of a class annotated by {@link Program} or by
	 *            {@link Executable}
	 * @return the values of the attributes or an empty map if the parameter is
	 *         <code>null</code> or if the class is not annotated by
	 *         {@link Program} or by {@link Executable}
	 */
	public static Map<String, String> attributes(Object executable) {

		if (executable == null
				|| !Aspects.hasAspect(PerExecutable.class,
						executable.getClass())) {
			return empty;
		}
		PerExecutable perExecutable = Aspects.aspectOf(PerExecutable.class,
				executable.getClass());
		return perExecutable.executable.values(executable);
	}

}
