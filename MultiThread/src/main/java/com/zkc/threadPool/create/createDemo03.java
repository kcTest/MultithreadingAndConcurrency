package com.zkc.threadPool.create;

import com.zkc.util.Print;
import com.zkc.util.ThreadUtil;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * （1）在接收新的异步任务target执行目标实例时，如果池内所有线程繁忙，此线程池就会添加新线程来处理任务。
 * （2）此线程池不会对线程池大小进行限制，线程池大小完全依赖于操作系统（或者说JVM）能够创建的最大线程大小。
 * （3）如果部分线程空闲，也就是存量线程的数量超过了处理任务数量，就会回收空闲（60秒不执行任务）线程。
 * <p>  “可缓存线程池”的适用场景：
 * <p>     需要快速处理突发性强、耗时较短的任务场景，如Netty的NIO处理场景、REST API接口的瞬时削峰场景。
 * <p>     “可缓存线程池”的线程数量不固定，只要有空闲线程就会被回收；接收到的新异步任务执行目标，查看是否有线程处于空闲状态，如果没有就直接创建新的线程。
 * <p>  “可缓存线程池”的弊端：
 * <p>     线程池没有最大线程数量限制，如果大量的异步任务执行目标实例同时提交，可能会因创建线程过多而导致资源耗尽
 */
public class createDemo03 {
	public static void main(String[] args) {
		ExecutorService pool = Executors.newCachedThreadPool();
		for (int i = 0; i < MAX; i++) {
			pool.execute(new TargetTask());
			pool.submit(new TargetTask());
		}
		ThreadUtil.sleepSeconds(1000);
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
