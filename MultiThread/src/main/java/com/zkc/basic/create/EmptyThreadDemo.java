package com.zkc.basic.create;

import com.zkc.util.Print;

/**
 * 通过继承Thread类创建一个线程实例
 */
public class EmptyThreadDemo {
	public static void main(String[] args) {
		Thread thread = new Thread();
		Print.cfo("线程名称：" + thread.getName());
		Print.cfo("线程ID：" + thread.getId());
		Print.cfo("线程状态：" + thread.getState());
		Print.cfo("线程优先级：" + thread.getPriority());
		Print.cfo(Thread.currentThread().getName() + "运行结束.");
		/*
		Thread线程的target属性默认为null。所以在thread线程执行时，其run()方法其实什么也没有做，线程就执行完了。
		 */
		thread.start();
	}
}
