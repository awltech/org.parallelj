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

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.parallelj.AndSplit;
import org.parallelj.Link;
import org.parallelj.OrSplit;
import org.parallelj.XorSplit;
import org.parallelj.internal.kernel.KOutputLink;
import org.parallelj.internal.kernel.KProcedure;
import org.parallelj.internal.kernel.callback.Entry;
import org.parallelj.internal.kernel.split.KAndSplit;
import org.parallelj.internal.kernel.split.KOrSplit;
import org.parallelj.internal.kernel.split.KXorSplit;
import org.parallelj.internal.reflect.callback.MethodCallback;
import org.parallelj.internal.reflect.callback.MethodPredicate;

public class SplitFactory extends AnnotationBasedBuilderFactory {

	static class AndSplitBuilder extends SplitBuilder<AndSplit> {

		@Override
		public ElementBuilder links() {
			AndSplit split = this.getMember().getAnnotation(AndSplit.class);
			for (String s : split.value()) {
				this.getBuilder().olink(this.getName(), s);
			}
			return super.links();
		}

		@Override
		public ElementBuilder splits() {
			KProcedure procedure = this.getProcedure();
			procedure.setSplit(new KAndSplit(procedure));
			return super.splits();
		}

	}

	static class OrSplitBuilder extends SplitBuilder<OrSplit> {

		@Override
		public ElementBuilder links() {
			OrSplit split = this.getMember().getAnnotation(OrSplit.class);
			for (Link l : split.value()) {
				KOutputLink link = this.getBuilder().olink(this.getName(),
						l.to());
				if (!("".equals(l.predicate().trim()))) {
					link.setPredicate(new MethodPredicate(SplitFactory
							.getPredicate(this.getBuilder().getType(), l
									.predicate())));
				}
			}
			return super.links();
		}

		@Override
		public ElementBuilder splits() {
			KProcedure procedure = this.getProcedure();
			procedure.setSplit(new KOrSplit(procedure));
			return super.splits();
		}
	}

	static class XorSplitBuilder extends SplitBuilder<XorSplit> {

		@Override
		public ElementBuilder links() {
			XorSplit split = this.getMember().getAnnotation(XorSplit.class);
			for (Link l : split.value()) {
				KOutputLink link = this.getBuilder().olink(this.getName(),
						l.to());
				if (!("".equals(l.predicate().trim()))) {
					link.setPredicate(new MethodPredicate(SplitFactory
							.getPredicate(this.getBuilder().getType(), l
									.predicate())));
				}
			}
			return super.links();
		}

		@Override
		public ElementBuilder splits() {
			KProcedure procedure = this.getProcedure();
			procedure.setSplit(new KXorSplit(procedure));
			return super.splits();
		}
	}

	static abstract class SplitBuilder<A extends Annotation> extends
			AnnotationBasedBuilder<A, Method> {

		@Override
		public ElementBuilder complete() {
			// if the procedure has the same method for entry, then there is
			// no exit. Otherwise, the method will be called twice!
			Entry entry = this.getProcedure().getEntry();
			if (entry instanceof MethodCallback) {
				MethodCallback callback = (MethodCallback) entry;
				if (!callback.getMethod().equals(this.getMember())) {
					this.getProcedure().setExit(
							new MethodCallback(this.getMember()));
				}
			}
			return super.complete();
		}

	}

	static final List<Class<? extends AnnotationBasedBuilder<?, ?>>> types = new ArrayList<Class<? extends AnnotationBasedBuilder<?, ?>>>();

	static {
		types.add(AndSplitBuilder.class);
		types.add(OrSplitBuilder.class);
		types.add(XorSplitBuilder.class);
	}

	public SplitFactory() {
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
