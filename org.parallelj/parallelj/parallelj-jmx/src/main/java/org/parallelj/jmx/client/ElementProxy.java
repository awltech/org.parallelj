/*
 *     ParallelJ, framework for parallel computing
 *
 *     Copyright (C) 2010, 2011 Atos Worldline or third-party contributors as
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

package org.parallelj.jmx.client;

import org.parallelj.mirror.Element;

import com.google.gson.JsonObject;

/**
 * Represents an implementation of an element
 * 
 * @author Laurent Legrand
 * 
 */
abstract class ElementProxy implements Element {

	/**
	 * The content of the element
	 */
	private JsonObject content;

	ClientReflection reflection;

	ElementProxy() {
	}

	public String getId() {
		return this.content.get("Id").getAsString();
	}

	JsonObject getContent() {
		return this.content;
	}

	void setContent(JsonObject content) {
		this.content = content;
	}

	ClientReflection getReflection() {
		return reflection;
	}

	void setReflection(ClientReflection reflection) {
		this.reflection = reflection;
	}

}
