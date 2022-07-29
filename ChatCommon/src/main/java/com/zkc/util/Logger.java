package com.zkc.util;

public class Logger {
	
	
	/**
	 * 按指定格式输出字符串，源调用方所处方法体的方法名在前
	 *
	 * @param s 待输出字符串
	 */
	public static void debug(String s) {
		
		/*
		 %[argument_index$][flags][width][.precision]conversion
		 The width is the minimum number of characters to be written to the output.
		 If the length of the converted value is less than the width then the output will be padded by '  ' ('\u0020')
		 until the total number of characters equals the width. The padding is on the left by default.
		 If the '-' flag is given, then the padding will be on the right.
		 If the width is not specified then there is no minimum.
		*/
		System.out.printf("%20s |>  %s %n", ReflectionUtil.getCallMethod(), s);
	}
	
	/**
	 * 线程名+类名+方法名称输出
	 */
	synchronized public static void info(Object... args) {
		StringBuilder sb = new StringBuilder();
		for (Object arg : args) {
			sb.append(arg == null ? "null" : arg.toString()).append(" ");
		}
		System.out.printf("%20s |>  %s%n", String.format("[%s|%s]", Thread.currentThread().getName(), ReflectionUtil.getCallMethod()), sb);
	}
	
}
