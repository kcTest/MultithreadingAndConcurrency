package com.zkc.explicitLock;

import com.zkc.util.Print;
import com.zkc.util.ThreadUtil;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Condition类的await方法和Object类的wait方法等效。
 * Condition类的signal方法和Object类的notify方法等效。
 * Condition类的signalAll方法和Object类的notifyAll方法等效。
 * <p>
 * 一个Condition对象的signal（或signalAll）方法不能去唤醒其他Condition对象上的await线程
 */
public class ReentrantLockDemo02 {
	
	public static void main(String[] args) {
		//创建等待线程
		Thread waitThread = new Thread(new WaitTargetTask(), "waitThread");
		waitThread.start();
		
		//wait线程先执行
		ThreadUtil.sleepSeconds(1);
		
		//创建通知线程
		Thread notifyThread = new Thread(new NotifyTargetTask(), "notifyThread");
		notifyThread.start();
	}
	
	/**
	 * 创建一个显式锁
	 */
	private static final Lock LOCK = new ReentrantLock();
	/**
	 * 不能独立创建一个Condition对象，而是需要借助于显式锁实例去获取其绑定的Condition对象
	 */
	private static final Condition CONDITION = LOCK.newCondition();
	
	/**
	 * 等待线程用到的异步目标任务
	 */
	private static class WaitTargetTask implements Runnable {
		
		@Override
		public void run() {
			//1、抢锁
			LOCK.lock();
			try {
				Print.tcfo("我是等待方");
				//2、开始等待 并且释放锁 await()方法会让当前线程加入Condition对象等待队列中
				CONDITION.await();
				Print.tcfo("收到通知，等待方继续执行");
			} catch (InterruptedException e) {
				e.printStackTrace();
			} finally {
				//释放锁
				LOCK.unlock();
			}
		}
	}
	
	/**
	 * 通知线程用到的异步目标任务
	 */
	private static class NotifyTargetTask implements Runnable {
		
		@Override
		public void run() {
			//3、抢锁
			LOCK.lock();
			try {
				Print.tcfo("我是通知方");
				//4、发送通知 从Condition对象等待队列中唤醒一个线程 这个被唤醒的线程仍然需要重新尝试抢占锁
				CONDITION.signal();
				Print.tcfo("我发出通知了，但线程还没有立马释放");
			} finally {
				//5、释放锁之后 等待线程才能获得锁
				LOCK.unlock();
			}
		}
	}
}
