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

package org.parallelj.internal.util.sm;

import java.util.EventObject;

/**
 * The {@link StateEvent} is a generic event that can be used by any class that
 * uses an {@link Enum} to describe its state.
 * 
 * @author Atos Worldline
 *
 * @param <E> the class that ...
 * @param <S> the enum listing the available states.
 */
public class StateEvent<E, S extends Enum<S>> extends EventObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * The state if the event.
	 */
	private S state;
	
	/**
	 * Sole constructor
	 * 
	 * @param source the source of the event.
	 * @param state the new state.
	 */
	public StateEvent(E source, S state) {
		super(source);
		this.state = state;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public E getSource() {
		return (E)super.getSource();
	}

	/**
	 * Return the state of the source.
	 * 
	 */
	public S getState() {
		return state;
	}
	
	

}
