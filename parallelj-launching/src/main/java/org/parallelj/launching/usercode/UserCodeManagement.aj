package org.parallelj.launching.usercode;

import java.lang.reflect.Method;

public aspect UserCodeManagement {

	declare parents: org.parallelj.internal.kernel.KProgram implements IUserReturnCode;

	/*
	 * Add the interface IUserReturnCodeCode to the KProgram
	*/
	public String IUserReturnCode.errorCode = "";
	public Method IUserReturnCode.getterMethod;
	
	public String IUserReturnCode.getUserReturnCode() {
		return this.errorCode;
	}
	
	public void IUserReturnCode.setUserReturnCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public void IUserReturnCode.setUserReturnCodeGetterMethod(Method getterFieldMethod) {
		this.getterMethod = getterFieldMethod;
	}

	public Method IUserReturnCode.getUserReturnCodeGetterMethod() {
		return this.getterMethod;
	}

}
