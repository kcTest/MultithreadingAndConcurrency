package com.zkc.basic.create;

import com.zkc.util.Print;
import com.zkc.util.ThreadUtil;

/**
 * 通过匿名类优雅地创建Runnable线程目标类
 * <p>
 * Runnable接口是一个函数式接口，在接口实现时可以使用Lambda表达式提供匿名实现
 */
public class CreateDemo02_02 {
	
	public static void main(String[] args) {
		for (int i = 0; i < 2; i++) {
			Thread thread = new Thread(() -> {
				for (int i1 = 0; i1 < MAX; i1++) {
					Print.cfo(ThreadUtil.getCurThreadName() + ",轮次：" + i1);
				}
				Print.cfo(ThreadUtil.getCurThreadName() + " 运行结束.");
			}, "RunnableThread" + threadNo++);
			thread.start();
		}
		Print.cfo(Thread.currentThread().getName() + " 运行结束.");
	}
	
	private static final int MAX = 5;
	private static int threadNo = 1;
	
}
