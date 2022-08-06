package com.zkc.explicitLock;

import com.zkc.util.Print;
import com.zkc.util.ThreadUtil;


import java.util.concurrent.locks.LockSupport;

/**
 * 让线程在任意位置阻塞和唤醒
 */
public class LockSupportDemo {
	
	public static void main(String[] args) {
		
		ChangeObjectThread t1 = new ChangeObjectThread("线程一");
		ChangeObjectThread t2 = new ChangeObjectThread("线程二");
		//启动线程一
		t1.start();
		ThreadUtil.sleepSeconds(1);
		//启动线程二
		t2.start();
		ThreadUtil.sleepSeconds(1);
		//中断线程一
		t1.interrupt();
		//唤醒线程二  如果在LockSupport.park()前执行也是允许的
		LockSupport.unpark(t2);
	}
	
	private static class ChangeObjectThread extends Thread {
		public ChangeObjectThread(String name) {
			super(name);
		}
		
		@Override
		public void run() {
			Print.tco("即将进入无限时阻塞");
			//阻塞当前线程
			LockSupport.park();
			if (Thread.currentThread().isInterrupted()) {
				Print.tco("被中断了，但仍然会继续执行");
			} else {
				Print.tco("被重新唤醒了");
			}
		}
	}
}
