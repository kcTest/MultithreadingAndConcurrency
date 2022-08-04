package com.zkc.atomic;

import com.zkc.util.Print;
import com.zkc.util.ThreadUtil;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;

/**
 * [main|AtomicLongVSLongAdder:testAtomicLong]: 运行时长：0.031
 * [main|AtomicLongVSLongAdder:testAtomicLong]: 累加结果为：10000
 * [main|AtomicLongVSLongAdder:testLongAdder]: 运行时长：0.03
 * [main|AtomicLongVSLongAdder:testLongAdder]: 累加结果为：10000
 * <p>
 * [main|AtomicLongVSLongAdder:testAtomicLong]: 运行时长：0.172
 * [main|AtomicLongVSLongAdder:testAtomicLong]: 累加结果为：10000000
 * [main|AtomicLongVSLongAdder:testLongAdder]: 运行时长：0.061
 * [main|AtomicLongVSLongAdder:testLongAdder]: 累加结果为：10000000
 * <p>
 * [main|AtomicLongVSLongAdder:testAtomicLong]: 运行时长：1.273
 * [main|AtomicLongVSLongAdder:testAtomicLong]: 累加结果为：100000000
 * [main|AtomicLongVSLongAdder:testLongAdder]: 运行时长：0.159
 * [main|AtomicLongVSLongAdder:testLongAdder]: 累加结果为：100000000
 */
public class AtomicLongVSLongAdder {
	
	//每个线程的执行轮数 1000、1000_000、1000_000_0
	private static final int ADD = 1_000_000_0;
	
	/**
	 * 对比测试用例一：调用AtomicLong完成10个线程累加累加ADD次
	 * <p>
	 * 大量线程同时并发修改一个AtomicLong时，可能有很多线程会不停地自旋
	 * 大量的CAS空自旋会浪费大量的CPU资源，大大降低了程序的性能
	 */
	@Test
	public void testAtomicLong() {
		//并发任务数
		final int taskCount = 10;
		//获取CPU密集型任务线程池
		ThreadPoolExecutor pool = ThreadUtil.getCPUIntenseTargetThreadPool();
		//定义原子对象
		AtomicLong atomicLong = new AtomicLong(0);
		
		CountDownLatch latch = new CountDownLatch(taskCount);
		long start = System.currentTimeMillis();
		
		for (int i = 0; i < taskCount; i++) {
			pool.submit(() -> {
				for (int j = 0; j < ADD; j++) {
					atomicLong.incrementAndGet();
				}
				latch.countDown();
			});
		}
		try {
			latch.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		float time = (System.currentTimeMillis() - start) / 1000f;
		
		//输出统计结果
		Print.tcfo("运行时长：" + time);
		Print.tcfo("累加结果为：" + atomicLong.get());
	}
	
	/**
	 * 对比测试用例二：调用LongAdder完成10个线程累加ADD次
	 * <p>
	 * LongAdder提升高并发场景下CAS操作的性能
	 */
	@Test
	public void testLongAdder() {
		//并发任务数
		final int taskCount = 10;
		//获取CPU密集型任务线程池
		ThreadPoolExecutor pool = ThreadUtil.getCPUIntenseTargetThreadPool();
		//定义原子对象
		LongAdder longAdder = new LongAdder();
		
		CountDownLatch latch = new CountDownLatch(taskCount);
		long start = System.currentTimeMillis();
		
		for (int i = 0; i < taskCount; i++) {
			pool.submit(() -> {
				for (int j = 0; j < ADD; j++) {
					longAdder.add(1);
				}
				latch.countDown();
			});
		}
		try {
			latch.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		float time = (System.currentTimeMillis() - start) / 1000f;
		
		//输出统计结果
		Print.tcfo("运行时长：" + time);
		Print.tcfo("累加结果为：" + longAdder.longValue());
	}
}
