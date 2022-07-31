package com.zkc.basic;

import com.zkc.util.Print;

/**
 * 合并的本质是：线程A需要在合并点等待，一直等到线程B执行完成，或者等待超时
 * 调用join()方法的优势是比较简单，劣势是join()方法没有办法直接取得乙方线程的执行结果
 */
public class JoinDemo {
	
	public static void main(String[] args) {
		Print.cfo("启动 thread1");
		Thread thread1 = new SleepThread();
		thread1.start();
		try {
			thread1.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		Print.cfo("启动 thread2");
		//启动第二条线程 并进行限时合并 等待时间为1秒
		Thread thread2 = new SleepThread();
		thread2.start();
		try {
			thread2.join(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		Print.cfo("线程运行结束.");
	}
	
	private static final int MAX_SLEEP_GAP = 20000;
	
	private static class SleepThread extends Thread {
		
		private static int threadSeqNumber = 1;
		
		public SleepThread() {
			super("sleepThread-" + threadSeqNumber++);
		}
		
		@Override
		public void run() {
			try {
				Print.cfo(getName() + ", 进入睡眠");
				Thread.sleep(MAX_SLEEP_GAP);
			} catch (InterruptedException e) {
				e.printStackTrace();
				Print.cfo(getName() + " 发生异常被中断.");
				return;
			}
			Print.cfo(getName() + " 运行结束.");
		}
	}
}
