package com.zkc.intrinsicLock;

import com.zkc.util.Print;
import com.zkc.util.ThreadUtil;

public class WaitNotifyDemo {
	
	public static void main(String[] args) {
		//创建并启动等待线程
		Thread waitThread = new Thread(new WaitTargetTask(), "等待线程");
		waitThread.start();
		ThreadUtil.sleepSeconds(1);
		
		//并启动通知线程
		Thread notifyThread = new Thread(new NotifyTargetTask(), "通知线程");
		notifyThread.start();
	}
	
	private static Object lock = new Object();
	
	//异步目标任务 对应线程内发起等待
	private static class WaitTargetTask implements Runnable {
		
		@Override
		public void run() {
			synchronized (lock) {
				try {
					Print.tco("启动等待");
					//等待被通知 同时释放lock监视器的owner权
					lock.wait();
					//如果收到通知 线程会进入lock监视器的EntryList
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				//再次获取lock监视器的owner权
				Print.cfo("收到通知, 当前线程继续执行");
			}
		}
	}
	
	//异步目标任务 对应线程内发起通知
	private static class NotifyTargetTask implements Runnable {
		@Override
		public void run() {
			//等waitThread释放后 这里获取lock对象监视器的owner权
			synchronized (lock) {
				//从屏幕读取输入 目的是阻塞通知线程 方便使用jStack查看线程状态
				Print.consoleInput();
				//此时不会立即释放lock的监视器的owner权 需要等执行完毕
				lock.notifyAll();
				Print.cfo("发出通知");
			}
		}
	}
	
	
}
