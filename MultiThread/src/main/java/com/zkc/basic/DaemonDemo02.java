package com.zkc.basic;

import com.zkc.util.Print;
import com.zkc.util.ThreadUtil;

/**
 * 在守护线程中创建的线程，新的线程都是守护线程。
 * 通过调用setDaemon(false)可以将新线程调整成用户线程
 */
public class DaemonDemo02 {
	
	public static void main(String[] args) {
		Thread daemonThread = new Thread(() -> {
			for (int i = 0; i < MAX; i++) {
				Thread normalThread = new NormalThread();
//				normalThread.setDaemon(false); 
				//main结束后没有用户线程 很快停止
				normalThread.start();
			}
		}, "daemonThread");
		daemonThread.setDaemon(true);
		daemonThread.start();
		ThreadUtil.sleepMilliseconds(SLEEP_GAP);
		Print.syncTco(daemonThread.getName() + " 状态为:" + daemonThread.getState());
		Print.syncTco(ThreadUtil.getCurThreadName() + " 运行结束");
	}
	
	private static final int SLEEP_GAP = 500;
	private static final int MAX = 5;
	
	private static class NormalThread extends Thread {
		
		private static int threadNo = 1;
		
		public NormalThread() {
			super("normalThread-" + threadNo++);
		}
		
		@Override
		public void run() {
			for (; ; ) {
				ThreadUtil.sleepMilliseconds(SLEEP_GAP);
				Print.syncTco(getName() + ", 守护状态为:" + isDaemon());
			}
		}
	}
}
