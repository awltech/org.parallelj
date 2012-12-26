package org.parallelj.launching.reflect;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.parallelj.internal.reflect.AnnotationBasedBuilderFactory;
import org.parallelj.internal.reflect.ElementBuilder;
import org.parallelj.launching.In;
import org.parallelj.launching.LaunchingMessageKind;
import org.parallelj.launching.inout.Argument;
import org.parallelj.launching.parser.Parser;
import org.parallelj.launching.quartz.ProgramJobsAdapter.IProgramInputOutputs;

public class InBuilderFactory extends AnnotationBasedBuilderFactory {

	public static class InBuilder extends AnnotationBasedBuilder<In, Field> {
		@Override
		public ElementBuilder complete() {
			// Get all the informations about the field annotated with @In
			String fieldName = this.getMember().getName();
			Class<?> fieldType = this.getMember().getType();
			Class<? extends Parser> parser = this.getMember().getAnnotation(In.class).parser();
			Method writeFieldMethod = null;
			// Get the setter method for the Field
			Class<?> programType = this.getBuilder().getType();
			try {
				for (PropertyDescriptor descriptor : Introspector.getBeanInfo(programType)
						.getPropertyDescriptors()) {
					if (descriptor.getName().equals(fieldName)) {
						 writeFieldMethod = descriptor.getWriteMethod();
					}
				}
			} catch (IntrospectionException e) {
				throw new RuntimeException(LaunchingMessageKind.ELAUNCH0004.format(programType, fieldName, e));
			}
			if (writeFieldMethod==null) {
				throw new RuntimeException(LaunchingMessageKind.ELAUNCH0004.format(programType, fieldName));
			}

			Argument argument = new Argument(fieldName, fieldType, parser, writeFieldMethod);
			// Add the argument to the KProgram
			((IProgramInputOutputs)this.getProgram()).addArgument(argument);
			
			return super.complete();
		}
	}

	public InBuilderFactory(
			List<Class<? extends AnnotationBasedBuilder<?, ?>>> types) {
		super(types);
	}

	static final List<Class<? extends AnnotationBasedBuilder<?, ?>>> types = new ArrayList<Class<? extends AnnotationBasedBuilder<?, ?>>>();

	static {
		types.add(InBuilder.class);
	}

	public InBuilderFactory() {
		super(types);
	}


}