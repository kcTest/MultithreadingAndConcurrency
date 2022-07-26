package com.zkc.util;

public class ReflectionUtil {
	
	/**
	 * 获取调用方当前所处方法体的名称
	 *
	 * @return 方法名称
	 */
	public static String getCallMethod() {
		StackTraceElement[] elements = Thread.currentThread().getStackTrace();
		return elements[3].getMethodName();
	}
}
