package com.zkc.util;

public class Print {
	
	/**
	 * 带类名和方法名输出
	 */
	synchronized public static void cfo(Object msg) {
		String cf = "[" + ReflectionUtil.getCallClassMethod() + "]";
		//提交线程池进行独立输出 使得输出不影响当前线程的执行
		ThreadUtil.sqlExecute(() -> {
			System.out.println(cf + "：" + msg);
		});
	}
	
	public static void tfo(String msg) {
		String s = "[" + Thread.currentThread().getName() + "]" + ": " + msg;
		ThreadUtil.sqlExecute(() -> {
			System.out.println(s);
		});
	}
}
