package com.zkc.intrinsicLock;

import com.zkc.util.Print;
import com.zkc.util.ThreadUtil;

/**
 * 必须在synchronized同步块的内部调用。
 * wait 主要作用是让当前线程阻塞并等待被唤醒:
 * 1）当线程调用了locko（某个同步锁对象）的wait()方法后，JVM会将当前线程加入locko监视器的WaitSet（等待集），等待被其他
 * 线程唤醒。
 * （2）当前线程会释放locko对象监视器的Owner权利，让其他线程可以抢夺locko对象的监视器。
 * （3）让当前线程等待，其状态变成WAITING。
 * <p>
 * notify 主要作用是唤醒在等待的线程
 * （1）当线程调用了locko（某个同步锁对象）的notify()方法后，JVM会唤醒locko监视器WaitSet中的第一条等待线程。
 * （2）当线程调用了locko的notifyAll()方法后，JVM会唤醒locko监视器WaitSet中的所有等待线程。
 * （3）等待线程被唤醒后，会从监视器的WaitSet移动到EntryList，线程具备了排队抢夺监视器Owner权利的资格，其状态从WAITING变成BLOCKED。
 * （4）EntryList中的线程抢夺到监视器的Owner权利之后，线程的状态从BLOCKED变成Runnable，具备重新执行的资格
 */
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
	
	private static final Object lock = new Object();
	
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
