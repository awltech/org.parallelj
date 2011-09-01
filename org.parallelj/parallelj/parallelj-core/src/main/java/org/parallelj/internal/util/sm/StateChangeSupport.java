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

import java.beans.PropertyChangeSupport;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;


/**
 * Helper classes that manage the set of listeners and the firing of a state
 * change.
 * 
 * Inspired by {@link PropertyChangeSupport}
 * 
 * @author Atos Worldline
 * 
 * @param <E>
 * @param <S>
 */
public class StateChangeSupport<E, S extends Enum<S>> {
	
	private static final StateListener[] EMPTY = new StateListener[0];

	/**
	 * The source of the event.
	 */
	private E source;

	private Set<StateListener<E, S>> listeners = Collections
			.synchronizedSet(new HashSet<StateListener<E, S>>());

	public StateChangeSupport(E source) {
		this.source = source;
	}

	public void addStateListener(StateListener<E, S> listener) {
		this.listeners.add(listener);
	}

	public void removeStateListener(StateListener<E, S> listener) {
		this.listeners.remove(listener);
	}

	public void fireStateChanded(S state) {
		StateEvent<E, S> event = new StateEvent<E, S>(this.source, state);
		for (StateListener<E, S> listener : this.listeners.toArray(EMPTY)) {
			listener.stateChanged(event);
		}
	}

}
