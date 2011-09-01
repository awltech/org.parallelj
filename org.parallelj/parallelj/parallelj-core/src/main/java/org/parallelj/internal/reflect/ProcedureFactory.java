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
import java.util.ServiceLoader;
import java.util.concurrent.Callable;

import org.parallelj.Program;
import org.parallelj.internal.kernel.KProgram;
import org.parallelj.internal.kernel.KProcedure;
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
			return method.getReturnType().getAnnotation(Program.class) != null;
		}

		@Override
		public KProcedure newProcedure(Method method, KProgram program) {
			SubProcessProcedure procedure = new SubProcessProcedure(program);
			procedure.setSubProgram(ProgramFactory.getProgram(method
					.getReturnType()));
			return procedure;
		}

	}

	private static ProcedureFactory[] builtin = {
			new SubProcessProcedureFactory(), new CallableProcedureFactory(),
			new RunnableProcedureFactory(), new NoopProcedureFactory() };

	private static ServiceLoader<ProcedureFactory> loader = ServiceLoader
			.load(ProcedureFactory.class);

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