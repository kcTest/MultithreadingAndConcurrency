package com.zkc.thread;

import com.zkc.util.Print;

/**
 * Java中使用抢占式调度模型进行线程调度。
 * priority实例属性的优先级越高，线程获得CPU时间片的机会就越多，但也不是绝对的
 */
public class PriorityDemo {
	
	public static void main(String[] args) throws InterruptedException {
		int length = 10;
		PriorityThread[] threads = new PriorityThread[length];
		for (int i = 0; i < length; i++) {
			threads[i] = new PriorityThread();
			threads[i].setPriority(i + 1);
		}
		for (int i = 0; i < length; i++) {
			threads[i].start();
		}
		Thread.sleep(1000);
		for (int i = 0; i < length; i++) {
			/*stop()实例方法可能导致资源状态不一致*/
			threads[i].stop();
		}
		for (int i = 0; i < length; i++) {
			Print.cfo(threads[i].getName() + "-优先级为:" + threads[i].getPriority() + " 机会值为:" + threads[i].opportunities);
		}
	}
	
	private static class PriorityThread extends Thread {
		
		private static int theadNo = 1;
		private long opportunities = 0;
		
		public PriorityThread() {
			super("thread-" + theadNo++);
		}
		
		@Override
		public void run() {
			for (; ; ) {
				opportunities++;
			}
		}
	}
}
