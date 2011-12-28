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

/**
 * KCondition is more or less an implementation of a Petri Net place.
 * 
 * @author Atos Worldline
 */
public class KCondition extends KElement {

	class Marking {

		short value = KCondition.this.initial;

	}

	/**
	 * The initial capacity of this condition.
	 * 
	 */
	short initial = 0;

	/**
	 * The maximum number of tokens that can be stored in this condition.
	 * 
	 */
	short capacity = Short.MAX_VALUE;

	/**
	 * Create a condition with a default capacity and a default initial value
	 * 
	 * @param program
	 *            the program that contains the condition.
	 */
	public KCondition(KProgram program) {
		super(program);
		this.program.addCondition(this);
	}

	/**
	 * Create a condition with a default capacity and a given initial value
	 * 
	 * @param program
	 *            the program that contains the condition.
	 * @param initial
	 *            the initial value.
	 */
	public KCondition(KProgram program, short initial) {
		this(program);
		this.initial = initial;
	}

	/**
	 * Create a condition with a given capacity and a given initial value
	 * 
	 * @param program
	 *            the program that contains the condition.
	 * @param initial
	 *            the initial value.
	 * @param capacity
	 *            the capacity.
	 */
	public KCondition(KProgram program, short initial, short capacity) {
		this(program, initial);
		this.capacity = capacity;
	}

	public void init(KProcess process) {
		this.setMarking(process, new Marking());
	}

	/**
	 * Verify if this condition contains tokens for a given process.
	 * 
	 * @param process
	 *            the process
	 * 
	 * @return <code>true</code> if there is at least one token.
	 *         <code>false</code> otherwise.
	 */
	public boolean contains(KProcess process) {
		return this.getMarking(process).value > 0;
	}

	/**
	 * Consume a token for a given process.
	 * 
	 * @param process
	 *            the process
	 * 
	 * @throws IllegalStateException
	 *             if {@link #isEmpty(KProcess)} is <code>true</code>.
	 */
	public void consume(KProcess process) {
		if (this.isEmpty(process)) {
			// TODO add message kind
			throw new IllegalStateException();
		}
		this.getMarking(process).value--;
	}

	/**
	 * Produce a token into the condition for a given process.
	 * 
	 * @param process
	 *            the process
	 */
	public void produce(KProcess process) {
		this.getMarking(process).value++;
	}

	/**
	 * Remove all tokens from this condition for a given process.
	 * 
	 * @param process
	 *            the process
	 * 
	 */
	public void reset(KProcess process) {
		this.getMarking(process).value = 0;
	}

	/**
	 * Return the number of tokens in this condition for a given process.
	 * 
	 * @param process
	 *            the process
	 * 
	 * @return the number of tokens in this condition
	 */
	public int size(KProcess process) {
		return this.getMarking(process).value;
	}

	@Override
	protected Marking getMarking(KProcess process) {
		return (Marking) super.getMarking(process);
	}

	/**
	 * Verify if this condition contains tokens or not for a given process.
	 * 
	 * @param process
	 *            the process
	 * @return <code>true</code> if this condition is empty. <code>false</code>
	 *         otherwise.
	 */
	public boolean isEmpty(KProcess process) {
		return this.size(process) == 0;
	}

	/**
	 * @return the capacity
	 */
	public short getCapacity() {
		return this.capacity;
	}

	/**
	 * @param capacity
	 *            the capacity to set
	 */
	public void setCapacity(short capacity) {
		this.capacity = capacity;
	}

	/**
	 * @return the initial value
	 */
	public short getInitial() {
		return this.initial;
	}

	/**
	 * @param initial
	 *            the initial value to set
	 */
	public void setInitial(short initial) {
		this.initial = initial;
	}

}
