package com.zkc.util;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.LockSupport;

public class ThreadUtil {
	
	/**
	 * CPU核心个数
	 */
	private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
	
	public static void sqlExecute(Runnable command) {
		getSeqOrScheduledExecutorService().execute(command);
	}
	
	public static ScheduledExecutorService getSeqOrScheduledExecutorService() {
		return SeqOrScheduledTargetThreadPoolLazyHolder.EXECUTOR;
	}
	
	public static Thread getCurrentThread() {
		return Thread.currentThread();
	}
	
	/**
	 * 懒汉式单例创建线程池 用于定时任务、顺序排队执行任务
	 */
	private static class SeqOrScheduledTargetThreadPoolLazyHolder {
		private static final ScheduledThreadPoolExecutor EXECUTOR = new ScheduledThreadPoolExecutor(1, new CustomThreadFactory("seq"));
		
		static {
			Runtime.getRuntime().addShutdownHook(
					new ShutdownHookThread("定时和顺序任务线程池", () -> {
						//优雅关闭线程池
						shutdownThreadPoolGracefully(EXECUTOR);
						return null;
					}));
		}
	}
	
	/**
	 * 定制线程工厂
	 */
	private static class CustomThreadFactory implements ThreadFactory {
		/**
		 * 线程池数量
		 */
		private static final AtomicInteger poolNumber = new AtomicInteger(1);
		private final ThreadGroup group;
		/**
		 * 线程数量
		 */
		private final AtomicInteger threadNumber = new AtomicInteger(1);
		private final String threadTag;
		
		public CustomThreadFactory(String threadTag) {
			SecurityManager manager = System.getSecurityManager();
			group = manager != null ? manager.getThreadGroup() : Thread.currentThread().getThreadGroup();
			this.threadTag = "appPool-" + poolNumber.getAndIncrement() + "-" + threadTag + "-";
		}
		
		@Override
		public Thread newThread(Runnable target) {
			Thread thread = new Thread(group, target, threadTag + threadNumber.getAndIncrement(), 0);
			if (thread.isDaemon()) {
				thread.setDaemon(false);
			}
			if (thread.getPriority() != Thread.NORM_PRIORITY) {
				thread.setPriority(Thread.NORM_PRIORITY);
			}
			return thread;
		}
	}
	
	public static void shutdownThreadPoolGracefully(ExecutorService threadPool) {
		if (threadPool == null || threadPool.isTerminated()) {
			return;
		}
		try {
			threadPool.shutdown();
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		try {
			//等待60s 等待线程池中的任务执行完成
			if (!threadPool.awaitTermination(60, TimeUnit.SECONDS)) {
				//调用 shutdownNow 取消正在执行的任务
				threadPool.shutdownNow();
				//再次等待60s 如果还未结束 可以再次尝试 或者直接放弃
				if (!threadPool.awaitTermination(60, TimeUnit.SECONDS)) {
					System.err.println("线程池中任务未正常执行结束");
				}
			}
		} catch (InterruptedException e) {
			//捕获异常 重新调用shutdown
			threadPool.shutdownNow();
		}
		//仍然没有关闭 循环关闭1000次 每次等待10毫秒
		if (!threadPool.isTerminated()) {
			try {
				for (int i = 0; i < 1000; i++) {
					if (threadPool.awaitTermination(10, TimeUnit.MILLISECONDS)) {
						break;
					}
					threadPool.shutdownNow();
				}
			} catch (Throwable e) {
				System.err.println(e.getMessage());
			}
		}
		
	}
	
	/**
	 * 获取当前线程名称
	 */
	public static String getCurThreadName() {
		return Thread.currentThread().getName();
	}
	
	/**
	 * 线程睡眠 单位:毫秒
	 */
	public static void sleepMilliseconds(int millisecond) {
		LockSupport.parkNanos(millisecond * 1000L * 1000L);
	}
	
	/**
	 * 线程睡眠 单位:秒
	 */
	public static void sleepSeconds(int second) {
		LockSupport.parkNanos(second * 1000L * 1000L * 1000L);
	}
}
