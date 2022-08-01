package com.zkc.threadPool;

import com.zkc.util.Print;
import com.zkc.util.ThreadUtil;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 调用ThreadFactory的唯一方法newThread()创建新线程时，可以更改所创建的新线程的名称、线程组、优先级、守护进程状态等。
 * 如果newThread()的返回值为null，表示线程工厂未能成功创建线程，线程池可能无法执行任何任务
 */
public class CustomThreadFactory {
	
	public static void main(String[] args) {
		ExecutorService pool = Executors.newFixedThreadPool(2, new SimpleThreadFactory());
		for (int i = 0; i < MAX; i++) {
			pool.submit(new TargetTask());
		}
		ThreadUtil.sleepSeconds(10);
		Print.tco("关闭线程池");
		pool.shutdown();
	}
	
	private static final int MAX = 5;
	private static final int SLEEP_GAP = 500;
	
	
	private static class SimpleThreadFactory implements ThreadFactory {
		private static AtomicInteger threadNo = new AtomicInteger(1);
		
		/**
		 * 实现创建线程方法
		 */
		@Override
		public Thread newThread(Runnable target) {
			String threadName = "simpleThread-" + threadNo.getAndIncrement();
			Print.tco("创建一个线程，名称为：" + threadName);
			//设置执行目标和线程名称
			Thread thread = new Thread(target, threadName);
			thread.setDaemon(true);
			return thread;
		}
	}
	
	private static class TargetTask implements Runnable {
		private String taskName;
		private static AtomicInteger taskNo = new AtomicInteger(1);
		
		public TargetTask() {
			this.taskName = "task-" + taskNo.getAndIncrement();
		}
		
		@Override
		public void run() {
			Print.tco("任务:" + taskName + " doing");
			//线程睡眠
			ThreadUtil.sleepMilliseconds(SLEEP_GAP);
			Print.tco(taskName + " 运行结束.");
		}
		
		@Override
		public String toString() {
			return String.format("TargetTask{%s}", taskName);
		}
	}
	
}
