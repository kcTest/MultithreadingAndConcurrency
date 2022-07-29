package com.zkc.util;

public class ReflectionUtil {
	
	/**
	 * 获取源调用方当前所处方法体的名称
	 *
	 * @return 方法名称
	 */
	public static String getCallMethod() {
		StackTraceElement[] elements = Thread.currentThread().getStackTrace();
		return elements[3].getMethodName();
	}
	
	/**
	 * 获得调用方法的类名+方法名,带上中括号
	 */
	public static String getCallClassMethod() {
		StackTraceElement[] stack = Thread.currentThread().getStackTrace();
		String[] className = stack[3].getClassName().split("\\.");
		return className[className.length - 1] + ":" + stack[3].getMethodName();
	}
	
}
