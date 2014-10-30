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

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ProceduresOnError {
	
	Map<Object, Exception> mapProceduresInError = new ConcurrentHashMap<Object, Exception>(); 
	Map<Object, Exception> mapProceduresHandledInError = new ConcurrentHashMap<Object, Exception>(); 
		
	public void addProcedureInError(Object procedure, Exception exception) {
		mapProceduresInError.put(procedure, exception);
	}

	public void addProcedureHandledInError(Object procedure, Exception exception) {
		mapProceduresHandledInError.put(procedure, exception);
	}

	public boolean isExceptionForProcedure(Object procedure) {
		return mapProceduresInError.get(procedure)!=null;
	}
	
	public boolean isExceptionHandledForProcedure(Object procedure) {
		return mapProceduresHandledInError.get(procedure)!=null;
	}
	
	public boolean isErrorOfType(Class<?> error) {
		for(Map.Entry<Object, Exception> entry:this.mapProceduresInError.entrySet()) {
			if (entry.getValue().getClass().equals(error)) {
				return true;
			}
		}
		return false;
	}
	
	public boolean isHandledErrorOfType(Class<?> error) {
		for(Map.Entry<Object, Exception> entry:this.mapProceduresHandledInError.entrySet()) {
			if (entry.getValue().getClass().equals(error)) {
				return true;
			}
		}
		return false;
	}
	
	public boolean isErrorForProcedureOfType(Class<?> proc) {
		for(Object entry:this.mapProceduresInError.keySet()) {
			if (entry.getClass().equals(proc)) {
				return true;
			}
		}
		return false;
	}
	
	public boolean isHandledErrorForProcedureOfType(Class<?> proc) {
		for(Object entry:this.mapProceduresHandledInError.keySet()) {
			if (entry.getClass().equals(proc)) {
				return true;
			}
		}
		return false;
	}
	
	public Exception getExceptionForProcedure(Object procedure) {
		try {
			return mapProceduresInError.get(procedure);
		} catch (Exception e) {
			return null;
		}
	}
	
	public Exception getHandledExceptionForProcedure(Object procedure) {
		try {
			return mapProceduresHandledInError.get(procedure);
		} catch (Exception e) {
			return null;
		}
	}
	
	public Exception getAndRemoveExceptionForProcedure(Object procedure) {
		try {
			return mapProceduresInError.remove(procedure);
		} catch (Exception e) {
			return null;
		}
	}
	
	public Exception getAndRemoveHandledExceptionForProcedure(Object procedure) {
		try {
			return mapProceduresHandledInError.remove(procedure);
		} catch (Exception e) {
			return null;
		}
	}
	
	public long getNumberOfProceduresInError() {
		return mapProceduresInError.size();
	}

	public long getNumberOfHandledProceduresInError() {
		return mapProceduresHandledInError.size();
	}

	public Set<Object> getProceduresInError() {
		return mapProceduresInError.keySet();
	}

	public Set<Object> getProceduresHandledInError() {
		return mapProceduresHandledInError.keySet();
	}

	@Override
	public String toString() {
		String format = "[%s [%s]] ";
		StringBuffer strb = new StringBuffer();
		for (Object procedure : mapProceduresInError.keySet()) {
			strb.append(String.format(format, procedure, mapProceduresInError.get(procedure)));
		}
		
		return strb.toString();
	}

	
}
