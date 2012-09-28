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
import java.util.ArrayList;
import java.util.List;

import org.parallelj.PipelineParameter;
import org.parallelj.internal.MessageKind;
import org.parallelj.internal.kernel.KProcedure;
import org.parallelj.internal.kernel.loop.KPipelineForEachLoop;
import org.parallelj.internal.reflect.callback.FieldIterable;
import org.parallelj.internal.util.Classes;

public class PipelineForEachFactory extends ElementBuilderFactory {

	static class PipelineForEachBuilder extends ElementBuilder {

		Method method;

		PipelineParameter pipelineParameter;

		public PipelineForEachBuilder(Method method, PipelineParameter pipelineParameter) {
			this.method = method;
			this.pipelineParameter = pipelineParameter;
		}

		@Override
		public ElementBuilder complete() {
			Field field = Classes.findField(this.getBuilder().getType(),
					this.pipelineParameter.value());
			if (field == null) {
				MessageKind.W0001.format(this.pipelineParameter.value(), this
						.getBuilder().getType());
				return super.complete();
			}
			if (!Iterable.class.isAssignableFrom(field.getType())) {
				MessageKind.W0002.format(this.pipelineParameter.value(),
						Iterable.class);
				return super.complete();
			}
			field.setAccessible(true);
			KProcedure procedure = this.getBuilder().getProcedure(
					this.method.getName());
			KPipelineForEachLoop loop = new KPipelineForEachLoop(procedure);
			loop.setIterable(new FieldIterable(field));
			return super.complete();
		}

	}

	@Override
	public ElementBuilder newBuilder(Class<?> type) {
		List<ElementBuilder> builders = new ArrayList<ElementBuilder>();

		while (type != null) {
			for (Method method : type.getDeclaredMethods()) {
				Annotation[][] annotations = method.getParameterAnnotations();
				if (annotations.length == 0) {
					continue;
				}
				for (Annotation annotation : annotations[0]) {
					if (annotation.annotationType()
							.equals(PipelineParameter.class)) {
						builders.add(new PipelineForEachBuilder(method,
								(PipelineParameter) annotation));
					}
				}
			}
			type = type.getSuperclass();
		}
		return new CompositeElementBuilder(
				builders.toArray(new ElementBuilder[0]));
	}

}
