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
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.parallelj.AndJoin;
import org.parallelj.Begin;
import org.parallelj.OrJoin;
import org.parallelj.XorJoin;
import org.parallelj.internal.kernel.KProcedure;
import org.parallelj.internal.kernel.KProgram;
import org.parallelj.internal.kernel.join.KAndJoin;
import org.parallelj.internal.kernel.join.KOrJoin;
import org.parallelj.internal.kernel.join.KXorJoin;
import org.parallelj.internal.reflect.callback.MethodCallback;

public class JoinFactory extends AnnotationBasedBuilderFactory {

	/**
	 * Builder that manages the {@link Begin} annotation.
	 * 
	 * It creates a link between the {@link KProgram#getInputCondition() input
	 * condition} and the current procedure.
	 * 
	 * It creates an {@link KXorJoin} if no join has been set to the procedure
	 * 
	 * @author Laurent Legrand
	 * 
	 */
	static class BeginBuilder extends JoinBuilder<Begin> {

		@Override
		public ElementBuilder links() {
			this.getBuilder().ilink(
					this.getProgram().getInputCondition().getName(),
					this.getName());
			return super.links();
		}

		@Override
		public ElementBuilder complete() {
			if (this.getProcedure().getJoin() == null) {
				this.getProcedure().setJoin(new KXorJoin(this.getProcedure()));
			}

			return super.complete();
		}
	}
	
	static class AndJoinBuilder extends JoinBuilder<AndJoin> {

		@Override
		public ElementBuilder links() {
			AndJoin join = this.getMember().getAnnotation(AndJoin.class);
			for (String s : join.value()) {
				this.getBuilder().ilink(s, this.getName());
			}
			return super.links();
		}

		@Override
		public ElementBuilder joins() {
			KProcedure procedure = this.getProcedure();
			procedure.setJoin(new KAndJoin(procedure));
			return super.joins();
		}

	}

	static class XorJoinBuilder extends JoinBuilder<XorJoin> {

		@Override
		public ElementBuilder links() {
			XorJoin join = this.getMember().getAnnotation(XorJoin.class);
			for (String s : join.value()) {
				this.getBuilder().ilink(s, this.getName());
			}
			return super.links();
		}

		@Override
		public ElementBuilder joins() {
			KProcedure procedure = this.getProcedure();
			procedure.setJoin(new KXorJoin(procedure));
			return super.joins();
		}

	}

	static class OrJoinBuilder extends JoinBuilder<OrJoin> {

		@Override
		public ElementBuilder links() {
			OrJoin join = this.getMember().getAnnotation(OrJoin.class);
			for (String s : join.value()) {
				this.getBuilder().ilink(s, this.getName());
			}
			return super.links();
		}

		@Override
		public ElementBuilder joins() {
			KProcedure procedure = this.getProcedure();
			procedure.setJoin(new KOrJoin(procedure));
			return super.joins();
		}

	}

	static class JoinBuilder<A extends Annotation> extends
			AnnotationBasedBuilder<A, Method> {

		@Override
		public ElementBuilder procedures() {
			this.getBuilder().newProcedure(this.getMember());
			return super.procedures();
		}

		@Override
		public ElementBuilder complete() {
			this.getProcedure().setEntry(new MethodCallback(this.getMember()));
			return super.complete();
		}
	}

	static final List<Class<? extends AnnotationBasedBuilder<?, ?>>> types = new ArrayList<Class<? extends AnnotationBasedBuilder<?, ?>>>();

	static {
		types.add(BeginBuilder.class);
		types.add(AndJoinBuilder.class);
		types.add(XorJoinBuilder.class);
		types.add(OrJoinBuilder.class);
	}

	public JoinFactory() {
		super(types);
	}

}
