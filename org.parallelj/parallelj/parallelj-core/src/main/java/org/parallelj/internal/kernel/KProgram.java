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
package org.parallelj.internal.kernel;

import java.util.ArrayList;
import java.util.List;

import org.parallelj.mirror.ExceptionHandlingPolicy;
import org.parallelj.mirror.Procedure;
import org.parallelj.mirror.ProgramType;

/**
 * TODO javadoc
 * 
 * @author Atos Worldline
 */
public class KProgram implements ProgramType {

	/**
	 * List of elements contained in this program.
	 */
	List<KElement> elements = new ArrayList<KElement>();

	/**
	 * List of conditions.
	 */
	List<KCondition> conditions = new ArrayList<KCondition>();

	/**
	 * Collection of procedures.
	 */
	List<KProcedure> procedures = new ArrayList<KProcedure>();

	/**
	 * The input condition.
	 */
	protected KCondition inputCondition = new KCondition(this, (short) 1);

	/**
	 * The output condition.
	 */
	protected KCondition outputCondition = new KCondition(this);

	/**
	 * The condition corresponding to the liveness of this program.
	 */
	protected KCondition liveness = new KCondition(this);

	/**
	 * The Exception handling policy.
	 */
	protected ExceptionHandlingPolicy exceptionHandlingPolicy = ExceptionHandlingPolicy.RESUME;

	/**
	 * id of the program.
	 */
	private String id;

	/**
	 * The name of the program.
	 */
	private String name = "<undef>";

	/**
	 * Create a new program.
	 */
	public KProgram() {
		KReflection.getInstance().register(this);
	}

	/**
	 * Create a new {@link KProcess}
	 * 
	 * @param context
	 *            context linked to the program
	 * 
	 * @return a new program.
	 */
	public KProcess newProcess(Object context) {
		KProcess process = new KProcess(this, context);
		this.init(process);
		return process;
	}

	/**
	 * Initialize a process.
	 * 
	 * @param program
	 */
	void init(KProcess process) {
		process.markings = new Object[this.elements.size()];
		for (KElement element : this.elements) {
			element.init(process);
		}
	}

	/**
	 * Add an element
	 * 
	 * @param element
	 *            the element to add
	 */
	void addElement(KElement element) {
		if (!this.elements.contains(element)) {
			element.setIndex(this.elements.size());
			this.elements.add(element);
		}
	}

	/**
	 * Add a condition
	 * 
	 * @param condition
	 *            the condition to add
	 */
	void addCondition(KCondition condition) {
		if (!this.conditions.contains(condition)) {
			this.conditions.add(condition);
		}
	}

	/**
	 * Add a procedure
	 * 
	 * @param procedure
	 *            the procedure to add
	 */
	void addProcedure(KProcedure procedure) {
		if (!this.procedures.contains(procedure)) {
			this.procedures.add(procedure);
		}
	}

	/**
	 * Set the capacity of the program
	 * 
	 * @see #liveness
	 * 
	 * @param capacity
	 *            the new capacity
	 */
	public void setCapacity(short capacity) {
		this.liveness.setCapacity(capacity);
	}

	/**
	 * Return the input condition of this program
	 * 
	 * @return the input condition of this program
	 */
	public KCondition getInputCondition() {
		return inputCondition;
	}

	/**
	 * Return the output condition of this program
	 * 
	 * @return the output condition of this program
	 */
	public KCondition getOutputCondition() {
		return outputCondition;
	}

	/**
	 * @return the list of conditions of this program
	 */
	public List<KCondition> getConditions() {
		return conditions;
	}

	public List<Procedure> getProcedures() {
		return new ArrayList<Procedure>(this.procedures);
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	/**
	 * Set the name of this program
	 * 
	 * @param name
	 *            the name
	 */
	public void setName(String name) {
		this.name = name;
	}

	public ExceptionHandlingPolicy getExceptionHandlingPolicy() {
		return exceptionHandlingPolicy;
	}

	/**
	 * Set the exception handling policy
	 * 
	 * @param policy
	 *            the exception handling policy
	 */
	public void setExceptionHandlingPolicy(ExceptionHandlingPolicy policy) {
		this.exceptionHandlingPolicy = policy;
	}

}
