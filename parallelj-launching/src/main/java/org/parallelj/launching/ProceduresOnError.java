package org.parallelj.launching;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ProceduresOnError {
	
	Map<Object, Exception> mapProcs = new ConcurrentHashMap<Object, Exception>(); 
		
	public void addProcedureInError(Object procedure, Exception exception) {
		mapProcs.put(procedure, exception);
	}

	public boolean isExceptionForProcedure(Object procedure) {
		return mapProcs.get(procedure)!=null;
	}
	
	public boolean isErrorOfType(Class<?> error) {
		for(Map.Entry<Object, Exception> entry:this.mapProcs.entrySet()) {
			if (entry.getValue().getClass().equals(error)) {
				return true;
			}
		}
		return false;
	}
	
	public boolean isErrorForProcedureOfType(Class<?> proc) {
		for(Object entry:this.mapProcs.keySet()) {
			if (entry.getClass().equals(proc)) {
				return true;
			}
		}
		return false;
	}
	
	public Exception getExceptionForProcedure(Object procedure) {
		try {
			return mapProcs.get(procedure);
		} catch (Exception e) {
			return null;
		}
	}
	
	public Exception getAndRemoveExceptionForProcedure(Object procedure) {
		try {
			return mapProcs.remove(procedure);
		} catch (Exception e) {
			return null;
		}
	}
	
	public long getNumberOfProceduresInError() {
		return mapProcs.size();
	}

	@Override
	public String toString() {
		String format = "[%s [%s]] ";
		StringBuffer strb = new StringBuffer();
		for (Object procedure : mapProcs.keySet()) {
			strb.append(String.format(format, procedure, mapProcs.get(procedure)));
		}
		
		return strb.toString();
	}
	
	
}
