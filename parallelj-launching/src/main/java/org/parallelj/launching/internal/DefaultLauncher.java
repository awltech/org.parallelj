package org.parallelj.launching.internal;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;

import org.parallelj.Programs;
import org.parallelj.internal.reflect.ProcessHelperImpl;
import org.parallelj.internal.reflect.ProgramAdapter;
import org.parallelj.launching.In;
import org.parallelj.launching.ProceduresOnError;
import org.parallelj.launching.parser.Parser;
import org.parallelj.launching.quartz.ProgramJobsAdapter;

public class DefaultLauncher {

	Class<?> program;

	private static class ArgEntry {
		public String argumentName;
		public String stringValue;

		public ArgEntry(String arg) {
			int valIndex = arg.indexOf('=');
			this.argumentName = arg.substring(0, valIndex);
			this.stringValue = arg.substring(valIndex + 1);
		}

		public Object getValue(Class<? extends Parser> parser) {
			Parser prs = null;
			try {
				prs = parser.newInstance();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
			return (prs != null ? prs.parse(this.stringValue) : null);
		}

		public Method getSetMethod(PropertyDescriptor[] propDescs) {
			for (PropertyDescriptor propertyDescriptor : propDescs) {
				if (propertyDescriptor.getName().equals(this.argumentName)) {
					return propertyDescriptor.getWriteMethod();
				}
			}
			return null;
		}

		private void invokeSetMethod(Object programInstance,
				Annotation annotation, PropertyDescriptor[] propDescs)
				throws IllegalArgumentException, IllegalAccessException,
				InvocationTargetException {
			Object value = getValue(((In) annotation).parser());

			// Get the set Method for the parameter
			Method setMethod = getSetMethod(propDescs);
			if (setMethod != null) {
				setMethod.invoke(programInstance, value);
			}
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String className = args[0];
		Class<?> programClass = null;
		Object programInstance = null;
		try {
			programClass = Class.forName(className);
			programInstance = programClass.newInstance();
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		
		try {
			ArgEntry[] arguments = new ArgEntry[args.length - 1];
			for (int cpt = 1; cpt < args.length; cpt++) {
				arguments[cpt - 1] = new ArgEntry(args[cpt]);
			}

			BeanInfo bi = Introspector.getBeanInfo(programClass);
			PropertyDescriptor[] propDescs = bi.getPropertyDescriptors();

			for (ArgEntry argEntry : arguments) {
				Field field = programClass
						.getDeclaredField(argEntry.argumentName);
				for (Annotation annotation : field.getAnnotations()) {
					if (annotation.annotationType().equals(In.class)) {
						// This is an @In parameter for the Program...

						argEntry.invokeSetMethod(programInstance, annotation,
								propDescs);
					}
				}
			}
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (ArrayIndexOutOfBoundsException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (IntrospectionException e) {
			e.printStackTrace();
		}

		if (programInstance != null) {
			// Run the Program
			ProcessHelperImpl<?> processhelper = (ProcessHelperImpl<?>)Programs.as(programInstance).execute().join();
			ProceduresOnError procOnError = ProgramJobsAdapter.getProceduresInErrors(processhelper.getProcess());
			if (procOnError != null && procOnError. getNumberOfProceduresInError()>0) {
				System.err.println("Program terminated with errors: "+procOnError);
			}
		}

	}

}
