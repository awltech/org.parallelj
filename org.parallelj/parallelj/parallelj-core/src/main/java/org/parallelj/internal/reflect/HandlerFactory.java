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

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.parallelj.Handler;
import org.parallelj.internal.kernel.KHandler;
import org.parallelj.internal.kernel.KProcedure;
import org.parallelj.internal.reflect.callback.MethodCallback;

public class HandlerFactory extends AnnotationBasedBuilderFactory {

	static class HandlerBuilder extends
			AnnotationBasedBuilder<Handler, Method> {
		
		KHandler kHandler;

		@Override
		public ElementBuilder handlers() {
			this.kHandler = this.getBuilder().newHandler(this.getMember());
			return super.handlers();
		}

		@Override
		public ElementBuilder complete() {
			this.kHandler.setEntry(new MethodCallback(this.getMember()));
			Handler handler = this.getMember().getAnnotation(Handler.class);
			for (String s: handler.value()) {
				KProcedure procedure = this.getBuilder().getProcedure(s);
				if (procedure != null) {
					procedure.setHandler(this.kHandler);
				}
			}
			return super.complete();
		}
	}

	static final List<Class<? extends AnnotationBasedBuilder<?, ?>>> types = new ArrayList<Class<? extends AnnotationBasedBuilder<?, ?>>>();

	static {
		types.add(HandlerBuilder.class);
	}

	public HandlerFactory() {
		super(types);
	}

}
