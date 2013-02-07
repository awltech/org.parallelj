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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.parallelj.internal.MessageKind;
import org.parallelj.internal.kernel.KCall;
import org.parallelj.internal.kernel.callback.Entry;
import org.parallelj.internal.kernel.callback.Exit;

public class MethodCallback implements Entry, Exit {

	Method method;

	public MethodCallback(Method method) {
		this.method = method;
	}

	@Override
	public void enter(KCall call) {
		try {
			Object o = this.method.invoke(call.getProcess().getContext(),
					call.getInputValues());
			call.setContext(o);
		} catch (IllegalArgumentException e) {
			MessageKind.E0004.format(this.method,call.getProcess().getContext().getClass().getCanonicalName(),e);
		} catch (IllegalAccessException e) {
			MessageKind.E0004.format(this.method,call.getProcess().getContext().getClass().getCanonicalName(),e);
		} catch (InvocationTargetException e) {
			MessageKind.E0004.format(this.method,call.getProcess().getContext().getClass().getCanonicalName(),e);
		}
	}

	@Override
	public void exit(KCall call) {
		try {
			Object[] values = call.getOutputValues();
			Object[] args = new Object[values.length + 1];
			args[0] = call.getContext();
			for (int i = 0; i < values.length; i++) {
				args[i + 1] = values[i];
			}
			this.method.invoke(call.getProcess().getContext(), args);
		} catch (IllegalArgumentException e) {
			MessageKind.E0004.format(this.method,call.getProcess().getContext().getClass().getCanonicalName(),e);
		} catch (IllegalAccessException e) {
			MessageKind.E0004.format(this.method,call.getProcess().getContext().getClass().getCanonicalName(),e);
		} catch (InvocationTargetException e) {
			MessageKind.E0004.format(this.method,call.getProcess().getContext().getClass().getCanonicalName(),e);
		}
	}

	public Method getMethod() {
		return method;
	}

}
