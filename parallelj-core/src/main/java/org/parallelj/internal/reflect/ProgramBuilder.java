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

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.parallelj.Capacity;
import org.parallelj.Program;
import org.parallelj.internal.MessageKind;
import org.parallelj.internal.kernel.KCondition;
import org.parallelj.internal.kernel.KHandler;
import org.parallelj.internal.kernel.KInputLink;
import org.parallelj.internal.kernel.KOutputLink;
import org.parallelj.internal.kernel.KProcedure;
import org.parallelj.internal.kernel.KProgram;
import org.parallelj.mirror.ExceptionHandlingPolicy;

/**
 * Build a program based on introspection.
 * 
 * The sequence of building is {@link #start()}, {@link #build()} and {@link #complete()}.
 * 
 * @author Laurent Legrand
 *
 */
public class ProgramBuilder {

	/**
	 * The list of conditions created by this builder.
	 */
	Map<String, KCondition> conditions = new HashMap<String, KCondition>();

	/**
	 * The list of procedures created by this builder.
	 */
	Map<String, KProcedure> procedures = new HashMap<String, KProcedure>();

	/**
	 * The type to introspect
	 * 
	 */
	private Class<?> type;

	/**
	 * The builder that will build other elements.
	 */
	private ElementBuilder builder;

	/**
	 * The program that will be built.
	 */
	private KProgram program;

	public ProgramBuilder(Class<?> type) {
		this.type = type;
	}

	public ProgramBuilder start() {
		this.builder = new CompositeElementBuilder(ElementBuilderFactory
				.newBuilders(this.type));
		this.builder.setBuilder(this);

		this.program = new KProgram();
		this.program.setName(type.getName());
		this.builder.start();
		return this;
	}

	public ProgramBuilder build() {
		// init
		MessageKind.I0001.format(this.type);
		Program p = type.getAnnotation(Program.class);
		program.getInputCondition().setName(p.inputCondition());
		program.getOutputCondition().setName(p.outputCondition());
		this.conditions.put(program.getInputCondition().getName(), program
				.getInputCondition());
		this.conditions.put(program.getOutputCondition().getName(), program
				.getOutputCondition());

		for (String s : p.conditions()) {
			this.newCondition(s);
		}

		// check the capacity
		Capacity capacity = this.type.getAnnotation(Capacity.class);
		if (capacity != null && capacity.value() > 0) {
			this.program.setCapacity(capacity.value());
		}
		
		// Check Exception Handling Policy
		this.program.setExceptionHandlingPolicy(p.exceptionHandlingPolicy());

		this.builder.build();
		return this;
	}

	public ProgramBuilder complete() {
		this.builder.complete();
		return this;
	}

	public KCondition newCondition(String name) {
		KCondition condition = new KCondition(program);
		condition.setName(name);
		this.conditions.put(name, condition);
		return condition;
	}

	public KHandler newHandler(Method method) {
		KHandler handler = new KHandler(program);
		handler.setName(method.getName());
		handler.setType(method.getReturnType().getName());
		this.procedures.put(handler.getName(), handler);
		return handler;
	}

	public KProcedure newProcedure(Method method) {
		KProcedure procedure = ProcedureFactory.getFactory(method)
				.newProcedure(method, program);
		procedure.setName(method.getName());
		procedure.setType(method.getReturnType().getName());
		this.procedures.put(procedure.getName(), procedure);
		return procedure;
	}

	public KInputLink ilink(String from, String to) {
		KCondition condition = this.getCondition(from);
		KProcedure procedure = this.getProcedure(to);
		return new KInputLink(condition, procedure);
	}

	public KOutputLink olink(String from, String to) {
		KProcedure procedure = this.getProcedure(from);
		KCondition condition = this.getCondition(to);
		if (condition != null) {
			return new KOutputLink(procedure, condition);
		} else {
			KProcedure target = this.getProcedure(to);
			KCondition from2to = this.newCondition(from + "2" + to);
			new KInputLink(from2to, target);
			return new KOutputLink(procedure, from2to);
		}
	}

	public KCondition getCondition(String name) {
		return this.conditions.get(name);
	}

	public KProcedure getProcedure(String name) {
		return this.procedures.get(name);
	}

	public Class<?> getType() {
		return type;
	}

	public KProgram getProgram() {
		return program;
	}

}
