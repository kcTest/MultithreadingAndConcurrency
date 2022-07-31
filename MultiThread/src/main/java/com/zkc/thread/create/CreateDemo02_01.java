package com.zkc.thread.create;

import com.zkc.util.Print;
import com.zkc.util.ThreadUtil;

/**
 * 通过匿名类优雅地创建Runnable线程目标类
 * <p>
 * 在实现Runnable编写target执行目标类时，如果target实现类是一次性类，可以使用匿名实例的形式
 */
public class CreateDemo02_01 {
	
	public static void main(String[] args) {
		for (int i = 0; i < 2; i++) {
			Thread thread = new Thread(new Runnable() {
				@Override
				public void run() {
					for (int i = 0; i < MAX; i++) {
						Print.cfo(ThreadUtil.getCurThreadName() + ",轮次：" + i);
					}
					Print.cfo(ThreadUtil.getCurThreadName() + " 运行结束.");
				}
			}, "RunnableThread" + threadNo++);
			thread.start();
		}
		Print.cfo(Thread.currentThread().getName() + " 运行结束.");
	}
	
	private static final int MAX = 5;
	private static int threadNo = 1;
	
}
