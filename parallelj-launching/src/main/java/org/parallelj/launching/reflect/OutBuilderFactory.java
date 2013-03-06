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
import org.parallelj.launching.LaunchingMessageKind;
import org.parallelj.launching.Out;
import org.parallelj.launching.inout.IProgramInputOutputs;
import org.parallelj.launching.inout.Output;

public class OutBuilderFactory extends AnnotationBasedBuilderFactory {

	public static class InBuilder extends AnnotationBasedBuilder<Out, Field> {
		@Override
		public ElementBuilder complete() {
			// Get all the informations about the field annotated with @Out
			String fieldName = this.getMember().getName();
			Class<?> fieldType = this.getMember().getType();
			Method readFieldMethod = null;
			// Get the getter method for the Field
			Class<?> programType = this.getBuilder().getType();
			try {
				for (PropertyDescriptor descriptor : Introspector.getBeanInfo(programType)
						.getPropertyDescriptors()) {
					if (descriptor.getName().equals(fieldName)) {
						 readFieldMethod = descriptor.getReadMethod();
					}
				}
			} catch (IntrospectionException e) {
				LaunchingMessageKind.ELAUNCH0005.format(programType, fieldName, e);
			}
			if (readFieldMethod==null) {
				LaunchingMessageKind.ELAUNCH0005.format(programType, fieldName, new Exception());
			}

			Output output = new Output(fieldName, fieldType, readFieldMethod);
			// Add the output to the KProgram
			((IProgramInputOutputs)this.getProgram()).addOutput(output);
			
			return super.complete();
		}
	}

	public OutBuilderFactory(
			List<Class<? extends AnnotationBasedBuilder<?, ?>>> types) {
		super(types);
	}

	static final List<Class<? extends AnnotationBasedBuilder<?, ?>>> types = new ArrayList<Class<? extends AnnotationBasedBuilder<?, ?>>>();

	static {
		types.add(InBuilder.class);
	}

	public OutBuilderFactory() {
		super(types);
	}


}
