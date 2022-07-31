package com.zkc.threadPool.create;

import com.zkc.util.Print;
import com.zkc.util.ThreadUtil;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * （1）单线程化的线程池中的任务是按照提交的次序顺序执行的。
 * （2）池中的唯一线程的存活时间是无限的。
 * （3）当池中的唯一线程正繁忙时，新提交的任务实例会进入内部的阻塞队列中，并且其阻塞队列是无界的。
 * <p> 总体来说，单线程化的线程池所适用的场景是：任务按照提交次序，一个任务一个任务地逐个执行的场景
 */
public class createDemo01 {
	public static void main(String[] args) {
		ExecutorService pool = Executors.newSingleThreadExecutor();
		for (int i = 0; i < MAX; i++) {
			pool.execute(new TargetTask());
			pool.submit(new TargetTask());
		}
		ThreadUtil.sleepSeconds(1000);
		/*
		执行shutdown()方法后，线程池状态变为SHUTDOWN，此时线程池将拒绝新任务，
		不能再往线程池中添加新任务，否则会抛出RejectedExecutionException异常。
		此时，线程池不会立刻退出，直到添加到线程池中的任务都已经处理完成才会退出。
		还有一个与shutdown()类似的方法，叫作shutdownNow()，执行shutdownNow()方法后，线程池状态会立刻变成STOP，
		并试图停止所有正在执行的线程，并且不再处理还在阻塞队列中等待的任务，会返回那些未执行的任务
		*/
		pool.shutdown();
	}
	
	private static final int SLEEP_GAP = 500;
	private static final int MAX = 5;
	
	//执行目标
	private static class TargetTask implements Runnable {
		
		private final String taskName;
		private static final AtomicInteger taskNo = new AtomicInteger(1);
		
		public TargetTask() {
			this.taskName = "task-" + taskNo.getAndIncrement();
		}
		
		@Override
		public void run() {
			Print.tco("任务：" + taskName + " doing");
			ThreadUtil.sleepMilliseconds(SLEEP_GAP);
			Print.tco(taskName + " 运行结束");
		}
	}
}
