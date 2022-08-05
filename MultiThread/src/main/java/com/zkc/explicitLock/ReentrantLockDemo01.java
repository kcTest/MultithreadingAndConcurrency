package com.zkc.explicitLock;

import com.zkc.util.Print;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * ReentrantLock是一个可重入的独占（或互斥）锁，其中两个修饰词的含义为：
 * （1）可重入的含义：表示该锁能够支持一个线程对资源的重复加锁，也就是说，一个线程可以多次进入同一个锁所同步的临界区代码
 * 块。比如，同一线程在外层函数获得锁后，在内层函数能再次获取该锁，甚至多次抢占到同一把锁。
 * （2）独占的含义：在同一时刻只能有一个线程获取到锁，而其他获取锁的线程只能等待，只有拥有锁的线程释放了锁后，其他的线程才能够获取锁
 * <p>
 * ReentrantLock还支持公平锁和非公平锁两种模式
 * <p>
 * 简单地使用ReentrantLock进行同步累加
 */
public class ReentrantLockDemo01 {
	
	public static void main(String[] args) {
		//每个线程执行轮数
		final int turns = 100;
		//线程数
		final int threads = 10;
		//线程池 多线程模拟测试
		ExecutorService pool = Executors.newFixedThreadPool(threads);
		
		//创建一个可重入、独占的锁对象
		Lock lock = new ReentrantLock();
		CountDownLatch latch = new CountDownLatch(threads);
		long start = System.currentTimeMillis();
		//10个线程并发执行
		for (int i = 0; i < threads; i++) {
			pool.submit(() -> {
				try {
					//累加1000次
					for (int j = 0; j < turns; j++) {
						//传入锁 执行一个累加
						IncrementData.lockAndFastIncrease(lock);
					}
					Print.tco("本线程累加完成");
				} catch (Exception e) {
					e.printStackTrace();
				}
				latch.countDown();
			});
		}
		
		try {
			//等待所有线程结束
			latch.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		float time = (System.currentTimeMillis() - start) / 1000f;
		//输出统计结果
		Print.tcfo("运行时长为：" + time);
		Print.tcfo("累加结果为：" + IncrementData.sum);
	}
	
}
