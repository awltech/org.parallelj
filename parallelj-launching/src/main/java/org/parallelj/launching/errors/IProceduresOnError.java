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
