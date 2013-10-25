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
