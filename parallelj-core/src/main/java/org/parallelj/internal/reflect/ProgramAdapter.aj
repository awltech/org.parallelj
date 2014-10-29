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
package org.parallelj.internal.reflect;

import java.util.*;

import javax.xml.bind.annotation.XmlTransient;

import org.parallelj.Programs;
import org.parallelj.Programs.ProcessHelper;
import org.parallelj.internal.kernel.KProgram;
import org.parallelj.internal.reflect.callback.FieldIterable;

@SuppressWarnings("unchecked")
public aspect ProgramAdapter {

	/**
	 * Inter type declaration in order to link annotated classes and process
	 * instance.
	 */
	@SuppressWarnings("rawtypes")
	@XmlTransient
	public ProcessHelperImpl Adapter.instance;

	/**
	 * Contains the iterators used by for each loop
	 * 
	 * @return
	 */
	@XmlTransient
	public Map<FieldIterable, Iterator<?>> Adapter.iterators = new java.util.concurrent.ConcurrentHashMap<FieldIterable, Iterator<?>>();

	declare parents:
		(@org.parallelj.Program *) implements Adapter;
	
	/**
	 * Program "inherit" from Executable.
	 * 
	 * Avoid classes annotated by @Program to be also annotated by @Executable
	 */
	declare @type:
		(@org.parallelj.Program *) : @org.parallelj.Executable;

	public static <E> ProcessHelper<E> as(E e) {
		if (e instanceof Adapter) {
			return ((Adapter) e).instance;
		}
		return null;
	}

	public static Map<FieldIterable, Iterator<?>> getIterators(Object object) {
		if (object instanceof Adapter) {
			return ((Adapter) object).iterators;
		}
		return null;
	}

	Map<FieldIterable, Iterator<?>> around(FieldIterable fieldIterable, Object object): execution(public Map<FieldIterable, Iterator<?>> FieldIterable.getIterators(..))
		&& target(fieldIterable)
		&& args(object) {
	
		return getIterators(object);
	}
	
	@SuppressWarnings("rawtypes")
	ProcessHelper around(Object object): execution(public static ProcessHelper Programs+.as(..))
		&& args(object) {
		return ProgramAdapter.as(object);
	}
	
	public static privileged aspect PerProgram pertypewithin(@org.parallelj.Program *) {

		KProgram program;

		after() : staticinitialization(@org.parallelj.Program *) {
			this.program = ProgramFactory.getProgram(thisJoinPoint
					.getSignature().getDeclaringType());
		}

		@SuppressWarnings("rawtypes")
		after(Object context): execution((@org.parallelj.Program *).new(..)) && this(context) {
			((Adapter) context).instance = new ProcessHelperImpl(this.program
					.newProcess(context));
		}

	}

}
