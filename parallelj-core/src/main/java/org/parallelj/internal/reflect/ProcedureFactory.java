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
package org.parallelj.internal.reflect;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ServiceLoader;
import java.util.concurrent.Callable;

import org.parallelj.Pipeline;
import org.parallelj.PipelineData;
import org.parallelj.Program;
import org.parallelj.internal.kernel.KProcedure;
import org.parallelj.internal.kernel.KProgram;
import org.parallelj.internal.kernel.misc.KPipeline;
import org.parallelj.internal.kernel.procedure.CallableProcedure;
import org.parallelj.internal.kernel.procedure.RunnableProcedure;
import org.parallelj.internal.kernel.procedure.SubProcessProcedure;

public abstract class ProcedureFactory {

	static class CallableProcedureFactory extends ProcedureFactory {

		@Override
		public boolean accept(Method method) {
			return Callable.class.isAssignableFrom(method.getReturnType());
		}

		@Override
		public KProcedure newProcedure(Method method, KProgram program) {
			return new CallableProcedure(program);
		}

	}

	static class NoopProcedureFactory extends ProcedureFactory {

		@Override
		public boolean accept(Method method) {
			return method.getReturnType().equals(Void.TYPE);
		}

		@Override
		public KProcedure newProcedure(Method method, KProgram program) {
			return new KProcedure(program);
		}

	}

	static class RunnableProcedureFactory extends ProcedureFactory {

		@Override
		public boolean accept(Method method) {
			return Runnable.class.isAssignableFrom(method.getReturnType());
		}

		@Override
		public KProcedure newProcedure(Method method, KProgram program) {
			return new RunnableProcedure(program);
		}

	}

	static class SubProcessProcedureFactory extends ProcedureFactory {

		@Override
		public boolean accept(Method method) {
			if (method.getReturnType().getAnnotation(Program.class) != null
					|| method.getReturnType().getAnnotation(Pipeline.class) != null) {
				return true;
			} else {
				return false;
			}
		}

		@Override
		public KProcedure newProcedure(Method method, KProgram program) {

			if (method.getReturnType().getAnnotation(Program.class) != null) {
				SubProcessProcedure procedure = new SubProcessProcedure(program);
				KProgram subProgram = ProgramFactory.getProgram(method
						.getReturnType());
				procedure.setSubProgram(subProgram);
				return procedure;
			} else if (method.getReturnType().getAnnotation(Pipeline.class) != null) {

				// taking pipeline inner class
				Class<?> type = method.getReturnType();

				SubProcessProcedure procedure = new SubProcessProcedure(program);
				KProgram pipeline = PipelineFactory.getPipeline(method
						.getReturnType());

				Field pipelineData = null;

				// finding filed with annotation PipelineData
				while (type != null) {
					for (Field field : type.getDeclaredFields()) {
						Annotation[] annotations = field.getAnnotations();

						if (annotations.length == 0) {
							continue;
						}

						for (Annotation annotation : annotations) {
							if (annotation.annotationType().equals(
									PipelineData.class)) {
								pipelineData = field;

							}
						}
					}
					type = type.getSuperclass();
				}

				KPipeline.pipeline(pipeline,
						pipeline.getKProcedures().toArray(new KProcedure[0]));
				procedure.setPipelineDataName(pipelineData.getName());
				procedure.setSubProgram(pipeline);
				procedure.setPipeline(true);
				return procedure;
			} else {
				return null;
			}
		}
	}

	private static ProcedureFactory[] builtin = {
			new SubProcessProcedureFactory(), new CallableProcedureFactory(),
			new RunnableProcedureFactory(), new NoopProcedureFactory() };

	private static ServiceLoader<ProcedureFactory> loader;
	static {
		ServiceLoader<ProcedureFactory> loader = ServiceLoader.load(ProcedureFactory.class, ProcedureFactory.class.getClassLoader());
		if (loader==null || loader.iterator()==null || !loader.iterator().hasNext()) {
			loader = ServiceLoader.load(ProcedureFactory.class, Thread.currentThread().getContextClassLoader());
		}
	}

	public abstract boolean accept(Method method);

	public abstract KProcedure newProcedure(Method method, KProgram program);

	public static ProcedureFactory getFactory(Method method) {
		for (ProcedureFactory factory : builtin) {
			if (factory.accept(method)) {
				return factory;
			}
		}
		for (ProcedureFactory factory : loader) {
			if (factory.accept(method)) {
				return factory;
			}
		}
		return null;
	}
}