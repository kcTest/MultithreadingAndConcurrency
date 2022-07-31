package com.zkc.thread;

import com.zkc.util.Print;

public class StackAreaDemo {
	public static void main(String[] args) throws InterruptedException {
		Thread thread = Thread.currentThread();
		Print.cfo("当前线程名称：" + thread.getName());
		Print.cfo("当前线程ID：" + thread.getId());
		Print.cfo("当前线程状态：" + thread.getState());
		Print.cfo("当前线程优先级：" + thread.getPriority());
		int a = 1, b = 1;
		int c = a / b;
		anotherFunc();
		Thread.sleep(1000000);
	}
	
	private static void anotherFunc() {
		int a = 1, b = 1;
		int c = a / b;
		anotherFunc2();
	}
	
	private static void anotherFunc2() {
		int a = 1, b = 1;
		int c = a / b;
	}
}
