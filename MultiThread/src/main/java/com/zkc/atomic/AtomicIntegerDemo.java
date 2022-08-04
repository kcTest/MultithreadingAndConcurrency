package com.zkc.atomic;

import com.zkc.util.Print;
import com.zkc.util.ThreadUtil;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * AtomicInteger：整型原子类。
 * AtomicLong：长整型原子类。
 * AtomicBoolean：布尔型原子类。
 * <p>
 * 基础原子类（以AtomicInteger为例）主要通过CAS自旋+volatile的方案实现，既保障了变量操作的线程安全性，又避免了
 * synchronized重量级锁的高开销，使得Java程序的执行效率大为提升
 */
public class AtomicIntegerDemo {
	
	@Test
	public void test01() {
		//定义一个整数原子类型实例 
		AtomicInteger i = new AtomicInteger(0);
		
		//取值 再设置一个新值  [AtomicIntegerDemo:main]: tempVal=0; i=3
		int tempVal = i.getAndSet(3);
		Print.fo("tempVal=" + tempVal + "; i=" + i.get());
		
		//取值 然后自增  [AtomicIntegerDemo:main]: tempVal=3; i=4
		tempVal = i.getAndIncrement();
		Print.fo("tempVal=" + tempVal + "; i=" + i.get());
		
		//取值 然后增加5 [AtomicIntegerDemo:main]: tempVal=4; i=9
		tempVal = i.getAndAdd(5);
		Print.fo("tempVal=" + tempVal + "; i=" + i.get());
		
		//CAS交换  [AtomicIntegerDemo:main]: flag=true; i=100
		boolean flag = i.compareAndSet(9, 100);
		Print.fo("flag=" + flag + "; i=" + i.get());
	}
	
	@Test
	public void test02() throws InterruptedException {
		final int threadCount = 10;
		CountDownLatch latch = new CountDownLatch(threadCount);
		AtomicInteger atomicInteger = new AtomicInteger(0);
		for (int i = 0; i < threadCount; i++) {
			ThreadUtil.getMixedTargetThreadPool().submit(() -> {
				for (int j = 0; j < 1000; j++) {
					atomicInteger.getAndIncrement();
				}
				latch.countDown();
			});
		}
		latch.await();
		Print.tco("累加之和：" + atomicInteger.get());
		/*
		10个线程每个线程累加1000次，结果为10000，该结果与预期结果相同。所以，对基础原子类实例的并发操作是线程安全的
		 */
	}
	
}
