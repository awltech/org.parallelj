package org.parallelj.launching.usercode;

import java.lang.reflect.Method;

public aspect UserCodeManagement {

	declare parents: org.parallelj.internal.kernel.KProgram 
		implements org.parallelj.launching.usercode.IUserReturnCode;

	/*
	 * Add the interface IUserReturnCodeCode to the KProgram
	*/
	public String org.parallelj.launching.usercode.IUserReturnCode.errorCode = "";
	public Method org.parallelj.launching.usercode.IUserReturnCode.getterReturnCodeMethod;
	
	public String org.parallelj.launching.usercode.IUserReturnCode.getUserReturnCode() {
		return this.errorCode;
	}
	
	public void org.parallelj.launching.usercode.IUserReturnCode.setUserReturnCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public void org.parallelj.launching.usercode.IUserReturnCode.setUserReturnCodeGetterMethod(Method getterFieldMethod) {
		this.getterReturnCodeMethod = getterFieldMethod;
	}

	public Method org.parallelj.launching.usercode.IUserReturnCode.getUserReturnCodeGetterMethod() {
		return this.getterReturnCodeMethod;
	}

}
