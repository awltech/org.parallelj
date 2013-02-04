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
import org.parallelj.launching.ErrorCode;
import org.parallelj.launching.In;
import org.parallelj.launching.LaunchingMessageKind;
import org.parallelj.launching.inout.Argument;
import org.parallelj.launching.parser.Parser;
import org.parallelj.launching.quartz.ProgramJobsAdapter.IErrorCode;
import org.parallelj.launching.quartz.ProgramJobsAdapter.IProgramInputOutputs;

public class ErrorCodeBuilderFactory extends AnnotationBasedBuilderFactory {

	public static class ErrorCodeBuilder extends AnnotationBasedBuilder<ErrorCode, Field> {
		@Override
		public ElementBuilder complete() {
			// Get all the informations about the field annotated with @ErrorCode
			String fieldName = this.getMember().getName();
//			Class<?> fieldType = this.getMember().getType();
			//Class<? extends Parser> parser = this.getMember().getAnnotation(ErrorCode.class).parser();
			Method getReadFieldMethod = null;
			// Get the setter method for the Field
			Class<?> programType = this.getBuilder().getType();
			try {
				for (PropertyDescriptor descriptor : Introspector.getBeanInfo(programType)
						.getPropertyDescriptors()) {
					if (descriptor.getName().equals(fieldName)) {
						getReadFieldMethod = descriptor.getReadMethod();
						 break;
					}
				}
			} catch (IntrospectionException e) {
				throw new RuntimeException(LaunchingMessageKind.ELAUNCH0006.format(programType, fieldName, e));
			}
			if (getReadFieldMethod==null) {
				throw new RuntimeException(LaunchingMessageKind.ELAUNCH0006.format(programType, fieldName));
			}

//			Argument argument = new Argument(fieldName, fieldType, parser, writeFieldMethod);
			// Add the argument to the KProgram
			((IErrorCode)this.getProgram()).setErrorCodeGetterMethod(getReadFieldMethod);
			return super.complete();
		}
	}

	public ErrorCodeBuilderFactory(
			List<Class<? extends AnnotationBasedBuilder<?, ?>>> types) {
		super(types);
	}

	static final List<Class<? extends AnnotationBasedBuilder<?, ?>>> types = new ArrayList<Class<? extends AnnotationBasedBuilder<?, ?>>>();

	static {
		types.add(ErrorCodeBuilder.class);
	}

	public ErrorCodeBuilderFactory() {
		super(types);
	}


}
