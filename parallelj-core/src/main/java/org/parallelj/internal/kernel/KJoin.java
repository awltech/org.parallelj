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
 * A join controls the enabling of an {@link KProcedure#isEnabled(KProcess)
 * KProcedure}.
 * 
 * The verification is perform through the {@link #isEnabled(KProcess)} method.
 * If the verification is successful, the {@link #join(KCall)} method will be
 * called.
 * 
 * @author Atos Worldline
 */
public interface KJoin {

	/**
	 * Verify if the {@link #join(KCall)} is enabled.
	 * 
	 * @param process
	 *            the process which holds the context
	 * 
	 * @return <code>true</code> if {@link #join(KCall)} is possible.
	 *         <code>false</code> otherwise.
	 */
	public abstract boolean isEnabled(KProcess process);

	/**
	 * Remove the tokens from the input conditions.
	 * 
	 * @param call
	 *            the call
	 * 
	 */
	public abstract void join(KCall call);
	
	public KElement getProcedure();

}
