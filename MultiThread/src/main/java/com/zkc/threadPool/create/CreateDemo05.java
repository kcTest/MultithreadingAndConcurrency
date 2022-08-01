package com.zkc.threadPool.create;

import com.zkc.util.Print;
import com.zkc.util.ThreadUtil;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * 总结起来，使用Executors创建线程池主要的弊端如
 * 下：
 * （1）FixedThreadPool和SingleThreadPool这两个工厂方法所创建的线程池，工作队列（任务排队的队列）
 * 的长度都为Integer.MAX_VALUE，可能会堆积大量的任务，从而导致OOM（即耗尽内存资源）。
 * （2）CachedThreadPool和ScheduledThreadPool
 * 这两个工厂方法所创建的线程池允许创建的线程数量为Integer.MAX_VALUE，可能会导致创建大量的线程，从而导致OOM。
 * 使用线程 池ThreadPoolExecutor的构造器，从而有效避免由于使用无界队列可能导致的内存资源耗尽，或者由于对线程个数不做限制而导致的CPU资源耗尽等问题
 * <p>
 * <p>
 * <p>
 * int corePoolSize, // 核心线程数，即使线程空闲（Idle），也不会回收
 * int maximumPoolSize, // 线程数的上限  如果当前工作线程数多于corePoolSize数量，但小于maximumPoolSize数量，那么仅当任务排队队列已满时才会创建新线程
 * long keepAliveTime, TimeUnit unit, // 线程最大空闲（Idle）时长  如果超过这个时间，默认情况下Idle、非Core线程会被回收
 * BlockingQueue<Runnable> workQueue, // 任务的排队队列 如果线程池的核心线程都在忙，那么所接收到的目标任务缓存在阻塞队列中
 * 在一个线程从一个空的阻塞队列中获取元素时线程会被阻塞，直到阻塞队列中有了元素
 * ThreadFactory threadFactory, //新线程的产生方式
 * RejectedExecutionHandler handler) // 拒绝策略 任务被拒绝有两种情况：
 * （1）线程池已经被关闭。
 * （2）工作队列已满且maximumPoolSize已满
 * <p>
 * <p>
 * （1）如果当前工作线程数量小于核心线程数量，执行器总是优先创建一个任务线程，而不是从线程队列中获取一个空闲线程。
 * （2）如果线程池中总的任务数量大于核心线程池数量，新接收的任务将被加入阻塞队列中，一直到阻塞队列已满。在核心线程池数量
 * <p>  已经用完、阻塞队列没有满的场景下，线程池不会为新任务创建一个新线程。
 * （3）当完成一个任务的执行时，执行器总是优先从阻塞队列中获取下一个任务，并开始执行，一直到阻塞队列为空，其中所有的缓存任务被取光。
 * （4）在核心线程池数量已经用完、阻塞队列也已经满了的场景下，如果线程池接收到新的任务，将会为新任务创建一个线程（非核心线程），并且立即开始执行新任务。
 * （5）在核心线程都用完、阻塞队列已满的情况下，一直会创建新线程去执行新任务，直到池内的线程总数超出maximumPoolSize。如果
 * <p>  线程池的线程总数超过maximumPoolSize，线程池就会拒绝接收任务，当新任务过来时，会为新任务执行拒绝策略
 */
public class CreateDemo05 {
	
	public static void main(String[] args) {
		/*
		  ThreadPoolExecutor线程池调度器为每个任务执行前后都提供了钩子方法
		  beforeExecute和afterExecute两个方法在每个任务执行前后被调用，
		  如果钩子（回调方法）引发异常，内部工作线程可能失败并突然终止
		 */
		ThreadPoolExecutor pool = new ThreadPoolExecutor(2, 4, 60, TimeUnit.SECONDS, new LinkedBlockingQueue<>(2)) {
			
			@Override
			protected void beforeExecute(Thread t, Runnable r) {
				Print.tco(r + " 前置钩子被执行");
				//记录执行时间
				START_TIME.set(System.currentTimeMillis());
				super.beforeExecute(t, r);
			}
			
			@Override
			protected void afterExecute(Runnable r, Throwable t) {
				super.afterExecute(r, t);
				//统计执行时长
				long time = System.currentTimeMillis() - START_TIME.get();
				Print.tco(r + " 后置钩子被执行，任务执行时长(ms)：" + time);
				//清空本地变量
				START_TIME.remove();
			}
			
			@Override
			protected void terminated() {
				Print.tco("调度器已经终止");
			}
		};
		for (int i = 0; i < MAX; i++) {
			pool.execute(new TargetTask());
		}
		ThreadUtil.sleepSeconds(10);
		Print.tco("关闭线程池");
		pool.shutdown();
	}
	
	/**
	 * 线程本地变量 用于记录线程异步任务的开始执行时间
	 */
	private static final ThreadLocal<Long> START_TIME = new ThreadLocal<>();
	private static final int SLEEP_GAP = 500;
	private static final int MAX = 5;
	
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
