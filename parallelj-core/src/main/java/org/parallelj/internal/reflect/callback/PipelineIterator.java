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
package org.parallelj.internal.reflect.callback;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

	private List list;

	/**
	 * Map for keeping track on procedure data index.
	 */
	private Map<String, Integer> map;

	public PipelineIterator() {
		map = new HashMap<String, Integer>();
	}

	public boolean contains(String id) {
		return map.containsKey(id);
	}

	public void add(String id) {
		map.put(id, 0);
	}

	/**
	 * Based on KProcedure index for data, it will return next value from list
	 * 
	 * @param call
	 * @return
	 */
	public Object getNext(KCall call) {
		if (list != null && !list.isEmpty()) {
			if (map.get(call.getProcedure().getId()) == null) {
				this.add(call.getProcedure().getId());
			}
			Integer integer = map.get(call.getProcedure().getId());
			Object object = list.get(integer);
			map.put(call.getProcedure().getId(), ++integer);
			return object;
		} else {
			return null;
		}
	}

	public List getList() {
		return list;
	}

	public void setList(List list) {
		this.list = list;
	}
}
