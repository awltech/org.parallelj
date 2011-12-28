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

import org.parallelj.internal.kernel.callback.Predicate;

/**
 * Represents an output link from a {@link KProcedure} to a {@link KCondition}.
 * 
 * @author Atos Worldline
 */
public class KOutputLink extends KLink {

	private Predicate predicate;

	/**
	 * Create a new output link from a {@link KProcedure} to a
	 * {@link KCondition}.
	 * 
	 * @param procedure
	 *            from
	 * @param condition
	 *            to
	 */
	public KOutputLink(KProcedure procedure, KCondition condition) {
		super(procedure, condition);
		condition.addOutputLink(this);
		procedure.addOutputLink(this);
	}

	/**
	 * Activate the link: produce token on the {@link KCondition}.
	 * 
	 * @param process
	 *            the process
	 */
	public void activate(KProcess process) {
		this.getCondition().produce(process);
	}

	/**
	 * A link may have a constraint (or predicate) associate to it.
	 * 
	 * @param process
	 *            the process
	 * 
	 * @return <code>true</code> if the constraint is verified.
	 *         <code>false</code> otherwise.
	 */
	public boolean verify(KProcess process) {
		return (this.predicate == null) ? true : this.predicate.verify(process);
	}

	/**
	 * Return the predicate associated to this link.
	 * 
	 * @return the predicate
	 */
	public Predicate getPredicate() {
		return predicate;
	}

	/**
	 * Set the predicate associated to this link.
	 * 
	 * @param predicate
	 *            the predicate
	 */
	public void setPredicate(Predicate predicate) {
		this.predicate = predicate;
	}

}
