package com.zkc.basic.create;

import com.zkc.util.Print;

/**
 * 继承Thread类创建线程类
 * （1）继承Thread类，创建一个新的线程类。
 * （2）重写run()方法，将需要并发执行的业务代码编写在run()方法中
 */
public class CreateDemo01 {
	
	public static void main(String[] args) {
		for (int i = 0; i < 2; i++) {
			Thread thread = new DemoThread();
			thread.start();
		}
		Print.cfo(Thread.currentThread().getName() + " 运行结束.");
	}
	
	private static final int MAX = 5;
	private static int threadNo = 1;
	
	private static class DemoThread extends Thread {
		
		public DemoThread() {
			super("DemoThread-" + threadNo++);
		}
		
		@Override
		public void run() {
			for (int i = 0; i < MAX; i++) {
				Print.cfo(getName() + ",轮次：" + i);
			}
			Print.cfo(getName() + " 运行结束.");
		}
	}
	
}
