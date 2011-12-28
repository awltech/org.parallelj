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
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.parallelj.internal.kernel.KProcedure;
import org.parallelj.internal.kernel.KProgram;
import org.parallelj.internal.util.Classes;

public class AnnotationBasedBuilderFactory extends ElementBuilderFactory {

	static class AnnotationBasedBuilder<A extends Annotation, M extends Member>
			extends ElementBuilder {

		private M member;

		public AnnotationBasedBuilder() {
		}

		public M getMember() {
			return member;
		}

		public void setMember(M member) {
			this.member = member;
		}

		public KProgram getProgram() {
			return this.builder.getProgram();
		}

		public String getName() {
			return this.member.getName();
		}

		protected KProcedure getProcedure() {
			return this.getBuilder().getProcedure(this.getName());
		}
		
	}

	Map<Class<? extends Annotation>, Class<AnnotationBasedBuilder<?, Method>>> methodBasedBuilders = new HashMap<Class<? extends Annotation>, Class<AnnotationBasedBuilder<?, Method>>>();

	Map<Class<? extends Annotation>, Class<AnnotationBasedBuilder<?, Field>>> fieldBasedBuilders = new HashMap<Class<? extends Annotation>, Class<AnnotationBasedBuilder<?, Field>>>();

	@SuppressWarnings("unchecked")
	public AnnotationBasedBuilderFactory(List<Class<? extends AnnotationBasedBuilder<?, ?>>> types) {
		for (Class<? extends AnnotationBasedBuilder<?, ?>> type : types) {
			List<Class<?>> typeArguments = Classes.getTypeArguments(
					AnnotationBasedBuilder.class, type);
			if (Method.class.equals(typeArguments.get(1))) {
				this.methodBasedBuilders.put(
						(Class<? extends Annotation>) typeArguments.get(0),
						(Class<AnnotationBasedBuilder<?, Method>>) type);
			} else {
				this.fieldBasedBuilders.put(
						(Class<? extends Annotation>) typeArguments.get(0),
						(Class<AnnotationBasedBuilder<?, Field>>) type);

			}
		}
	}

	@Override
	public ElementBuilder newBuilder(final Class<?> type) {

		List<ElementBuilder> builders = new ArrayList<ElementBuilder>();

		this.scanFields(type, builders);
		this.scanMethods(type, builders);

		return new CompositeElementBuilder(builders
				.toArray(new ElementBuilder[0]));

	}

	private void scanFields(Class<?> type, List<ElementBuilder> builders) {
		while (type != null) {
			for (Field field : type.getDeclaredFields()) {
				for (Map.Entry<Class<? extends Annotation>, Class<AnnotationBasedBuilder<?, Field>>> entry : this.fieldBasedBuilders
						.entrySet()) {
					if (field.getAnnotation(entry.getKey()) != null) {
						try {
							AnnotationBasedBuilder<?, Field> builder = entry
									.getValue().newInstance();
							builder.setMember(field);
							builders.add(builder);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}
			type = type.getSuperclass();
		}
	}

	private void scanMethods(Class<?> type, List<ElementBuilder> builders) {
		while (type != null) {
			for (Method method : type.getDeclaredMethods()) {
				for (Map.Entry<Class<? extends Annotation>, Class<AnnotationBasedBuilder<?, Method>>> entry : this.methodBasedBuilders
						.entrySet()) {
					if (method.getAnnotation(entry.getKey()) != null) {
						try {
							AnnotationBasedBuilder<?, Method> builder = entry
									.getValue().newInstance();
							builder.setMember(method);
							builders.add(builder);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}
			type = type.getSuperclass();
		}
	}

}
