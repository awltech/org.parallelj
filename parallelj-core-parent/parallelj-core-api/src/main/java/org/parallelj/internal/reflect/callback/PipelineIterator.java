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
package org.parallelj.internal.reflect.callback;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.parallelj.internal.MessageKind;
import org.parallelj.internal.kernel.KCall;

/**
 * Specialize iterator, which will not only iterate over list provided but also
 * keep track that which procedure is done with which data to ensure cascade
 * execution.
 * 
 * @author a169104
 * 
 */
public class PipelineIterator {

	private String pipelineDataName;

	/**
	 * Map for keeping track on procedure data index.
	 */
	private Map<String, Iterator> map;

	public PipelineIterator() {
		map = new HashMap<String, Iterator>();
	}

	public boolean contains(String id) {
		return map.containsKey(id);
	}

	public void add(String id, Iterator iterator) {
		map.put(id, iterator);
	}

	/**
	 * Based on KProcedure index for data, it will return next value from list
	 * 
	 * @param call
	 * @return
	 */
	public Object getNext(KCall call) {

		if (map.get(call.getProcedure().getId()) == null) {

			Object context = call.getProcess().getContext();

			// getting data by reflection
			Method readMethod = null;
			try {
				readMethod = new PropertyDescriptor(pipelineDataName,
						context.getClass()).getReadMethod();
			} catch (IntrospectionException e) {
				MessageKind.W0003.format(e);
			}
			Object invoke = null;
			try {
				invoke = readMethod.invoke(context);
			} catch (IllegalArgumentException e) {
				MessageKind.W0003.format(e);
			} catch (IllegalAccessException e) {
				MessageKind.W0003.format(e);
			} catch (InvocationTargetException e) {
				MessageKind.W0003.format(e);
			}

			// putting new Iterator into map
			if (invoke instanceof Iterable<?>) {
				Iterator newIterator = ((Iterable) invoke).iterator();
				this.add(call.getProcedure().getId(), newIterator);
			}
		}
		Iterator iterator = map.get(call.getProcedure().getId());
		return iterator.next();
	}
	
	public String getPipelineDataName() {
		return pipelineDataName;
	}

	public void setPipelineDataName(String pipelineDataName) {
		this.pipelineDataName = pipelineDataName;
	}
}
