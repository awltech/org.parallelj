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
package org.parallelj.mirror;

/**
 * Represents a state change event occurred in a {@link Machine}
 * 
 * @author Laurent Legrand
 * 
 * @param <E>
 */
public interface Event<E extends Enum<E>> {

	/**
	 * Return the machine that has generated this event
	 * 
	 * @param <M>
	 * @return the machine that has generated this event
	 */
	<M extends Machine<E>> M getSource();

	/**
	 * Return the kind of machine that has generated this event
	 * 
	 * @return the kind of machine that has generated this event
	 */
	MachineKind getMachineKind();

	/**
	 * Return the new state
	 * 
	 * @return the new state
	 */
	E getState();

	/**
	 * Return the worker (thread name) that called the state transition
	 * 
	 * @return the worker (thread name) that called the state transition
	 */
	String getWorker();

	/**
	 * Return the timestamp
	 * 
	 * @return the timestamp
	 */
	long getTimestamp();

}
