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
	
	public static void tco(String msg) {
		String s = "[" + Thread.currentThread().getName() + "]" + ": " + msg;
		ThreadUtil.sqlExecute(() -> {
			System.out.println(s);
		});
	}
	
	public static void syncTco(String msg) {
		System.out.println("[" + Thread.currentThread().getName() + "]" + ": " + msg);
	}
	
	/**
	 * 带方法名输出
	 */
	public static void fo(Object s) {
		String info = "[" + ReflectionUtil.getCallClassMethod() + "]";
		ThreadUtil.sqlExecute(() -> {
			System.out.println(info + ": " + s);
		});
		
	}
	
	/**
	 * 带线程名 类名 方法名
	 */
	public static void tcfo(String s) {
		String info = "[" + Thread.currentThread().getName() + "|" + ReflectionUtil.getCallClassMethod() + "]";
		ThreadUtil.sqlExecute(() -> {
			System.out.println(info + ": " + s);
		});
	}
}
