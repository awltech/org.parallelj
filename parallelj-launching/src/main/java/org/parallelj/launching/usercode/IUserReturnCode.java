package org.parallelj.launching.usercode;

import java.lang.reflect.Method;

public interface IUserReturnCode {
	public String getUserReturnCode();
	public void setUserReturnCode(String errorCode);
	public Method getUserReturnCodeGetterMethod();
	public void setUserReturnCodeGetterMethod(Method getterFieldMethod);
}

