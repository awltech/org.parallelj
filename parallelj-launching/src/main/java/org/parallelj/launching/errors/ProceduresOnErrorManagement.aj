package org.parallelj.launching.errors;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.parallelj.internal.MessageKind;
import org.parallelj.internal.kernel.KCall;
import org.parallelj.internal.kernel.KJoin;
import org.parallelj.internal.kernel.KProcess;
import org.parallelj.internal.kernel.KProgram;
import org.parallelj.internal.kernel.KSplit;
import org.parallelj.internal.kernel.loop.KWhileLoop;
import org.parallelj.internal.kernel.procedure.CallableProcedure;
import org.parallelj.internal.kernel.procedure.RunnableProcedure;
import org.parallelj.launching.OnError;
import org.parallelj.launching.errors.IProceduresOnError;

import debug.Debug;

privileged public aspect ProceduresOnErrorManagement {

	declare parents: org.parallelj.internal.kernel.KProgram implements IProceduresOnError;

	/*
	 * Add the interface IProceduresOnError to the KProgram
	*/
	public Method IProceduresOnError.getterMethod;
	public String IProceduresOnError.fieldName;
	private boolean IProceduresOnError.isErrors=false;
	private boolean IProceduresOnError.isHandledErrors=false;
	
	public void IProceduresOnError.setGetterMethod(Method getterFieldMethod) {
		this.getterMethod = getterFieldMethod;
	}

	public void IProceduresOnError.setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public String IProceduresOnError.getFieldName() {
		return this.fieldName;
	}
	
	public Method IProceduresOnError.getGetterMethod() {
		return this.getterMethod;
	}
	
	public synchronized void IProceduresOnError.addProcedureInError(KProgram kprogram, Object program, Object procedure, Exception exception) {
		ProceduresOnError obj;
		
		this.isErrors=true;
		Method method = ((IProceduresOnError)kprogram).getGetterMethod();
		if (method != null) {
			try {
				obj = (ProceduresOnError)method.invoke(program, new Object[]{});
				if (obj==null) {
					obj = new ProceduresOnError();
					Field field = program.getClass().getDeclaredField(((IProceduresOnError)kprogram).getFieldName());
					field.setAccessible(true);
					field.set(program, obj);
				}
				obj.addProcedureInError(procedure, exception);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}
	
	public synchronized void IProceduresOnError.addProcedureHandledInError(KProgram kprogram, Object program, Object procedure, Exception exception) {
		ProceduresOnError obj;
		
		this.isHandledErrors=true;
		Method method = ((IProceduresOnError)kprogram).getGetterMethod();
		if (method != null) {
			try {
				obj = (ProceduresOnError)method.invoke(program, new Object[]{});
				if (obj==null) {
					obj = new ProceduresOnError();
					Field field = program.getClass().getDeclaredField(((IProceduresOnError)kprogram).getFieldName());
					field.setAccessible(true);
					field.set(program, obj);
				}
				obj.addProcedureHandledInError(procedure, exception);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}
	
	public synchronized ProceduresOnError IProceduresOnError.getAllProceduresInError(Object program) {
		ProceduresOnError obj=null;
		if (this.getGetterMethod()!=null) {
			try {
				obj = (ProceduresOnError)this.getGetterMethod().invoke(program, new Object[]{});
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return obj; 
	}
	
	public boolean IProceduresOnError.isError() {
		return this.isErrors;
	}
	
	public boolean IProceduresOnError.isHandledError() {
		return this.isHandledErrors;
	}
	
    pointcut procsOnError(Object a): get(@OnError ProceduresOnError *.*) && this(a);
    
    before(Object procOnErrorField): procsOnError(procOnErrorField) {
    }
    
	/*
	 * Allow to get the procedures in error of a Process
	 */
	public static synchronized ProceduresOnError getProceduresInErrors(
			org.parallelj.mirror.Process process) {
		return ((IProceduresOnError)process.getProgram()).getAllProceduresInError(process.getContext());
	}


	// Interception around Runnable execution      
	void around(Object self): call(void complete())
		&& target(org.parallelj.internal.kernel.procedure.RunnableProcedure.RunnableCall)
		&& this(self)  {
		 
		RunnableProcedure.RunnableCall runnable = (RunnableProcedure.RunnableCall)thisJoinPoint.getTarget();
		RunnableProcedure procedure = (RunnableProcedure)runnable.getProcedure();
		if (runnable.getException() != null) {
			// There is an error !!!
			KProcess process = runnable.getProcess();
			if(procedure.getHandler() == null) {
				((IProceduresOnError)process.getProgram()).addProcedureInError(process.getProgram(), process.getContext(), runnable.getContext(), runnable.getException());
			} else {
				((IProceduresOnError)process.getProgram()).addProcedureHandledInError(process.getProgram(), process.getContext(), runnable.getContext(), runnable.getException());
			}
		}
		proceed(self);
	}

	// Interception around Callable execution      
	void around(Object self): call(void complete())
		&& target(org.parallelj.internal.kernel.procedure.CallableProcedure.CallableCall)
		&& this(self)  {

		CallableProcedure.CallableCall callable = (CallableProcedure.CallableCall)thisJoinPoint.getTarget();
		CallableProcedure procedure = (CallableProcedure)callable.getProcedure(); 
		if (callable.getException() != null) {
			// There is an error !!!
			KProcess process = callable.getProcess();
			if(procedure.getHandler() == null) {
				((IProceduresOnError)process.getProgram()).addProcedureInError(process.getProgram(), process.getContext(), callable.getContext(), callable.getException());
			} else {
				((IProceduresOnError)process.getProgram()).addProcedureHandledInError(process.getProgram(), process.getContext(), callable.getContext(), callable.getException());			}
		}
		proceed(self);
	}
	
	boolean around(org.parallelj.internal.kernel.KJoin self, org.parallelj.internal.kernel.KProcess process)
		:execution(* org.parallelj.internal.kernel.KJoin+.isEnabled(..))
		&& this(self) && args(process)
		&& within(org.parallelj.internal.kernel.loop.KWhileLoop) {
		
		try {
			return proceed(self, process);
		} catch (Exception e) {
			((IProceduresOnError)process.getProgram()).addProcedureInError(process.getProgram(), process.getContext(), process.getContext(), e);
			MessageKind.E0005.format(process.getContext(), e);
			return false;
		}

	}
	 
}
