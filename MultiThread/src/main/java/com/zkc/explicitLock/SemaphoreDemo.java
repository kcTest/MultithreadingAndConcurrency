package com.zkc.explicitLock;

import com.zkc.util.DateUtil;
import com.zkc.util.Print;

import java.util.GregorianCalendar;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 共享锁是在同一时刻允许多个线程持有的锁。获得共享锁的线程只能读取临界区的数据，不能修改临界区的数据
 * Semaphore（信号量）、ReadLock（读写锁）中的读锁、CountDownLatch倒数闩
 * <p>
 * Semaphore维护了一组虚拟许可，它的数量可以通过构造器的参数指定。线程在访问共享资源前必须调用Semaphore的acquire()方法获得许可，
 * 如果许可数量为0，该线程就一直阻塞。线程访问完资源后，必须调用Semaphore的release()方法释放许可
 */
public class SemaphoreDemo {
	
	public static void main(String[] args) throws InterruptedException {
		//排队总人数
		final int USERS = 10;
		//可以同时受理业务的窗口数量
		final int PERMITS = 2;
		CountDownLatch latch = new CountDownLatch(USERS);
		
		//创建信号量 2个许可
		Semaphore semaphore = new Semaphore(PERMITS);
		AtomicInteger index = new AtomicInteger(0);
		//任务
		Runnable r = () -> {
			try {
				//日历实例
				GregorianCalendar calendar = new GregorianCalendar();
				//抢占一个许可
				semaphore.acquire(1);
				//模拟业务操作 处理排队业务 
				Print.tco(DateUtil.getNowTime() + ", 受理中....服务号：" + index.getAndIncrement());
				//每一秒中只会有两个线程进入临界区
				Thread.sleep(1000);
				//释放一个信号
				semaphore.release(1);
			} catch (Exception e) {
				e.printStackTrace();
			}
			latch.countDown();
		};
		
		Thread[] threads = new Thread[USERS];
		for (int i = 0; i < USERS; i++) {
			threads[i] = new Thread(r, "线程" + i);
		}
		for (int i = 0; i < USERS; i++) {
			threads[i].start();
		}
		latch.await();
	}
	
}
