package com.zkc.thread;

import com.zkc.util.Print;
import com.zkc.util.ThreadUtil;

/**
 * 通过Jstack命令可以查看4个睡眠线程的状态，不过在此之前需要使用jps指令查找出以上程序对应的JVM进程SleepDemo的进程ID
 */
public class SleepDemo {
	
	public static void main(String[] args) {
		for (int i = 0; i < 5; i++) {
			Thread thread = new SleepThread();
			thread.start();
		}
		Print.cfo(ThreadUtil.getCurThreadName() + " 运行结束.");
	}
	
	/**
	 * 加大睡眠次数好使用jStack查看
	 */
	private static final int MAX = 50;
	private static final int MAX_SLEEP_GAP = 5000;
	
	private static class SleepThread extends Thread {
		private static int threadSeqNumber = 1;
		
		public SleepThread() {
			super("sleepThread-" + threadSeqNumber++);
		}
		
		@Override
		public void run() {
			try {
				for (int i = 1; i <= MAX; i++) {
					Print.cfo(getName() + ", 睡眠次数: " + i);
					//当前线程睡眠
					Thread.sleep(MAX_SLEEP_GAP);
				}
			} catch (InterruptedException e) {
				Print.cfo(getName() + " 发生异常被中断.");
			}
			Print.tco(getName() + " 运行结束.");
		}
	}
}
