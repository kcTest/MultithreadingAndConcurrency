package com.zkc.threadPool;

import com.zkc.util.Print;
import com.zkc.util.ThreadUtil;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 在线程池的任务缓存队列为有界队列（有容量限制的队列）的时候，如果队列满了，提交任务到线程池的时候就会被拒绝
 * <p>
 * （1）AbortPolicy
 * 使用该策略时，如果线程池队列满了，新任务就会被拒绝，并且抛出RejectedExecutionException异常。该策略是线程池默认的拒绝策略。
 * （2）DiscardPolicy
 * 该策略是AbortPolicy的Silent（安静）版本，如果线程池队列满了，新任务就会直接被丢掉，并且不会有任何异常抛出。
 * （3）DiscardOldestPolicy
 * 抛弃最老任务策略，也就是说如果队列满了，就会将最早进入队列的任务抛弃，从队列中腾出空间，再尝试加入队列。因为队列是队
 * 尾进队头出，队头元素是最老的，所以每次都是移除队头元素后再尝试入队。
 * （4）CallerRunsPolicy
 * 调用者执行策略。在新任务被添加到线程池时，如果添加失败，那么提交任务线程会自己去执行该任务，不会使用线程池中的线程去执行新任务。
 * <p>
 * <p>
 * 如果以上拒绝策略都不符合需求，那么可自定义一个拒绝策略，实现RejectedExecutionHandler接口的rejectedExecution方法
 */
public class CustomRejectPolicy {
	
	public static void main(String[] args) {
		int corePoolSize = 2, maximumPoolSize = 4;
		long keepAliveTime = 10;
		TimeUnit unit = TimeUnit.SECONDS;
		BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<>(2);
		ThreadFactory threadFactory = new SimpleThreadFactory();
		RejectedExecutionHandler rejectPolicy = new CustomIgnorePolicy();
		ThreadPoolExecutor poolExecutor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, rejectPolicy);
		//预启动所有核心线程
		poolExecutor.prestartAllCoreThreads();
		
		for (int i = 0; i < MAX; i++) {
			poolExecutor.execute(new TargetTask());
		}
		ThreadUtil.sleepSeconds(10);
		Print.tco("关闭线程池");
		poolExecutor.shutdown();
	}
	
	private static final int SLEEP_GAP = 500;
	private static final int MAX = 10;
	
	private static class CustomIgnorePolicy implements RejectedExecutionHandler {
		
		@Override
		public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
			//如 记录日志
			Print.tco(r + " rejected; " + " getTaskCount: " + executor.getTaskCount());
		}
	}
	
	private static class SimpleThreadFactory implements ThreadFactory {
		private static final AtomicInteger THREAD_NO = new AtomicInteger(1);
		
		/**
		 * 实现创建线程方法
		 */
		@Override
		public Thread newThread(Runnable target) {
			String threadName = "simpleThread-" + THREAD_NO.getAndIncrement();
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
