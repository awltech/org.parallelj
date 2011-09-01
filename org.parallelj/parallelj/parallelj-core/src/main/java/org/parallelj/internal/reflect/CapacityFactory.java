/*
 *     ParallelJ, framework for parallel computing
 *
 *     Copyright (C) 2010 Atos Worldline or third-party contributors as
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

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.parallelj.Capacity;

public class CapacityFactory extends AnnotationBasedBuilderFactory {

	static class CapacityBuilder extends
			AnnotationBasedBuilder<Capacity, Method> {

		@Override
		public ElementBuilder complete() {
			short capacity = this.getMember().getAnnotation(Capacity.class)
					.value();

			if (capacity > 0) {
				this.getProcedure().setCapacity(capacity);
			}
			return super.complete();
		}

	}

	static final List<Class<? extends AnnotationBasedBuilder<?, ?>>> types = new ArrayList<Class<? extends AnnotationBasedBuilder<?, ?>>>();

	static {
		types.add(CapacityBuilder.class);
	}

	public CapacityFactory() {
		super(types);
	}

	static Method getPredicate(Class<?> type, String name) {
		try {
			for (PropertyDescriptor descriptor : Introspector.getBeanInfo(type)
					.getPropertyDescriptors()) {
				if (descriptor.getName().equals(name)) {
					return descriptor.getReadMethod();
				}
			}
		} catch (IntrospectionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		throw new RuntimeException("No property found " + name + " in type "
				+ type);
	}

}
