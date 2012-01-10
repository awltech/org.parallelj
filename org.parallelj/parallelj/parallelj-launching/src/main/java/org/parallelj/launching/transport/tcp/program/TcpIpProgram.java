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
package org.parallelj.launching.transport.tcp.program;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.parallelj.internal.reflect.ProgramAdapter.Adapter;
import org.parallelj.launching.In;
import org.parallelj.launching.parser.Parser;


public class TcpIpProgram {
	private Class<? extends Adapter> adapterClass;
	
	private List<ArgEntry> argEntries = new ArrayList<ArgEntry>();
	
	public TcpIpProgram(Class<? extends Adapter> adapterClass) {
		this.adapterClass = adapterClass;

		// Search for annotation @In on attributes of
		// class clazz
		for (Field field : this.adapterClass.getDeclaredFields()) {
			// Search for an annotation @In
			for (Annotation annotation : field.getAnnotations()) {
				if (annotation.annotationType().equals(In.class)) {
					// Add the attribute where is the @In annotation
					// and
					// the Parser class
					Class<? extends Parser> parserClass = ((In) annotation)
							.parser();
					this.argEntries.add(new ArgEntry(field.getName(),
							field.getType(), parserClass));
				}
			}
		}

	}

	public List<ArgEntry> getArgEntries() {
		return argEntries;
	}

	public Class<? extends Adapter> getAdapterClass() {
		return adapterClass;
	}
	
	
}
