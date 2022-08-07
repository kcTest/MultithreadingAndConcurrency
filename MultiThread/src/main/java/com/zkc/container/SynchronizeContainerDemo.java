package com.zkc.container;

import com.zkc.util.Print;
import com.zkc.util.ThreadUtil;

import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * synchronized在线程没有发生争用的场景下处于偏向锁的状态，其性能是非常高的。
 * 一旦发生了线程争用，synchronized会由偏向锁膨胀成重量级锁，在抢占和释放时发生CPU内核态与用户态切换，会严重影响性能。
 */
public class SynchronizeContainerDemo {
	
	public static void main(String[] args) throws InterruptedException {
		//创建有序集合
		SortedSet<Integer> set = new TreeSet<>();
		set.add(1);
		set.add(2);
		//将有序集合包装成一个同步器 这些同步容器包装类在进行元素迭代时并不能进行元素添加操作
		Set<Integer> synchronizedSet = Collections.synchronizedSet(set);
		System.out.println("set: " + synchronizedSet);
		final int threads = 10;
		CountDownLatch latch = new CountDownLatch(threads);
		ThreadPoolExecutor pool = ThreadUtil.getCPUIntenseTargetThreadPool();
		for (int i = 0; i < threads; i++) {
			int curI = i + 3;
			pool.submit(() -> {
				synchronizedSet.add(curI);
				Print.tco(curI);
				latch.countDown();
			});
		}
		latch.await();
		System.out.println("set: " + synchronizedSet);
	}
}
