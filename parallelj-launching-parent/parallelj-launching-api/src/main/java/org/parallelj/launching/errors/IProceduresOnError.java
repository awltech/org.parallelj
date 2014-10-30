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
package org.parallelj.launching.errors;

import java.lang.reflect.Method;

import org.parallelj.internal.kernel.KProgram;

public interface IProceduresOnError {
	public void addProcedureInError(KProgram kprogram, Object program, Object proc, Exception exception);
	public void addProcedureHandledInError(KProgram kprogram, Object program, Object proc, Exception exception);
	public ProceduresOnError getAllProceduresInError(Object program);
	public Method getGetterMethod();
	public void setGetterMethod(Method getterFieldMethod);
	public void setFieldName(String fieldName);
	public String getFieldName();
	public boolean isError();
	public boolean isHandledError();
}
