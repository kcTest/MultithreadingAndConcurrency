package com.zkc.explicitLock;

import com.zkc.util.Print;
import com.zkc.util.ThreadUtil;

import java.util.concurrent.locks.ReentrantLock;

/**
 * 在JUC的显式锁Lock接口中，有以下两个方法可以用于可中断抢占：
 * （1）lockInterruptibly()
 * 可中断抢占锁抢占过程中会处理Thread.interrupt()中断信号，如果线程被中断，就会终止抢占并抛出InterruptedException异常。
 * （2）tryLock(long timeout,TimeUnit unit)
 * 阻塞式“限时抢占”（在timeout时间内）锁抢占过程中会处理Thread.interrupt()中断信号，如果线程被中断，就会终止抢占并抛出InterruptedException异常
 */
public class ReentrantLockDemo03 {
	
	public static void main(String[] args) throws InterruptedException {
		//创建可重入锁 默认非公平锁
		ReentrantLock lock = new ReentrantLock();
		
		//创建任务实例
		Runnable r = () -> IncrementData.lockInterruptiblyAndIncrease(lock);
		
		//创建第一个线程
		Thread t1 = new Thread(r, "thread-1");
		//创建第二个线程
		Thread t2 = new Thread(r, "thread-2");
		//启动第一个线程
		t1.start();
		//启动第二个线程
		t2.start();
		
		ThreadUtil.sleepMilliseconds(100);
		Print.syncTco("等待100ms, 中断俩个线程");
		
		t1.interrupt();
		t2.interrupt();
		
		Thread.sleep(Integer.MAX_VALUE);
	}
}
