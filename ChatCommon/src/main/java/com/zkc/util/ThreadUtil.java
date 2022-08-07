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
	
	
	/**
	 * 获取用于执行CPU密集型任务的线程池
	 * <p>
	 * CPU耗时所占的比例越高，需要的线程就越少
	 * <p>
	 * CPU密集型任务虽然也可以并行完成，但是并行的任务越多，花在任务切换的时间就越多，CPU执行任务的效率就越低，
	 * 所以要最高效地利用CPU，CPU密集型任务并行执行的数量应当等于CPU的核心数
	 */
	public static ThreadPoolExecutor getCPUIntenseTargetThreadPool() {
		return CPUIntenseTargetThreadPoolLazyHolder.EXECUTOR;
	}
	
	private static final int MAXIMUM_POOL_SIZE_CPU = CPU_COUNT;
	private static final int KEEP_ALIVE_SECONDS = 30;
	/**
	 * 有界队列
	 */
	private static final int QUEUE_SIZE = 10000;
	
	/**
	 * 懒汉式创建用于执行CPU密集型任务的线程池
	 */
	private static class CPUIntenseTargetThreadPoolLazyHolder {
		private static final ThreadPoolExecutor EXECUTOR = new ThreadPoolExecutor(
				MAXIMUM_POOL_SIZE_CPU, MAXIMUM_POOL_SIZE_CPU, KEEP_ALIVE_SECONDS, TimeUnit.SECONDS,
				new LinkedBlockingQueue<>(QUEUE_SIZE),
				new CustomThreadFactory("CPU")
		);
		
		static {
			EXECUTOR.allowCoreThreadTimeOut(true);
			Runtime.getRuntime().addShutdownHook(
					new ShutdownHookThread("IO密集型任务线程池", new Callable<Void>() {
						@Override
						public Void call() throws Exception {
							//优雅关闭线程池
							shutdownThreadPoolGracefully(EXECUTOR);
							return null;
						}
					})
			);
		}
	}
	
	/**
	 * 获取用于执行IO密集型任务的线程池
	 * <p>
	 * 由于IO密集型任务的CPU使用率较低，导致线程空余时间很多，因此通常需要开CPU核心数两倍的线程。
	 * 当IO线程空闲时，可以启用其他线程继续使用CPU，以提高CPU的使用率
	 */
	public static ThreadPoolExecutor getIOIntenseTargetThreadPool() {
		return IOIntenseTargetThreadPoolLazyHolder.EXECUTOR;
	}
	
	private static final int MAXIMUM_POOL_SIZE_IO = CPU_COUNT;
	
	private static class IOIntenseTargetThreadPoolLazyHolder {
		private static final ThreadPoolExecutor EXECUTOR = new ThreadPoolExecutor(
				MAXIMUM_POOL_SIZE_IO, MAXIMUM_POOL_SIZE_IO, KEEP_ALIVE_SECONDS, TimeUnit.SECONDS,
				new LinkedBlockingQueue<>(QUEUE_SIZE),
				new CustomThreadFactory("IO")
		);
		
		static {
			EXECUTOR.allowCoreThreadTimeOut(true);
			Runtime.getRuntime().addShutdownHook(
					new ShutdownHookThread("CPU密集型任务线程池", new Callable<Void>() {
						@Override
						public Void call() throws Exception {
							//优雅关闭线程池
							shutdownThreadPoolGracefully(EXECUTOR);
							return null;
						}
					})
			);
		}
	}
	
	/**
	 * 获取用于执行混合型密集型任务的线程池
	 *
	 * <p>
	 * 混合型任务既要执行逻辑计算，又要进行大量非CPU耗时操作（如RPC调用、数据库访问、网络通信等），
	 * 所以混合型任务CPU的利用率不是太高，非CPU耗时往往是CPU耗时的数倍
	 * <p>
	 * 最佳线程数 = ((线程等待时间+线程CPU时间) / 线程CPU时间) * CPU核数
	 */
	public static ThreadPoolExecutor getMixedTargetThreadPool() {
		return MixedTargetThreadPoolLazyHolder.EXECUTOR;
	}
	
	private static final int MAXIMUM_POOL_SIZE_MIXED = 128;
	private static final String MIXED_THREAD_AMOUNT = "mixed.thread.amount";
	
	private static class MixedTargetThreadPoolLazyHolder {
		
		private static final int MAX = System.getProperty(MIXED_THREAD_AMOUNT) != null ?
				Integer.parseInt(System.getProperty(MIXED_THREAD_AMOUNT)) : MAXIMUM_POOL_SIZE_MIXED;
		
		private static final ThreadPoolExecutor EXECUTOR = new ThreadPoolExecutor(
				MAX, MAX, KEEP_ALIVE_SECONDS, TimeUnit.SECONDS,
				new LinkedBlockingQueue<>(QUEUE_SIZE),
				new CustomThreadFactory("MIXED")
		);
		
		static {
			EXECUTOR.allowCoreThreadTimeOut(true);
			Runtime.getRuntime().addShutdownHook(
					new ShutdownHookThread("混合型任务线程池", new Callable<Void>() {
						@Override
						public Void call() throws Exception {
							//优雅关闭线程池
							shutdownThreadPoolGracefully(EXECUTOR);
							return null;
						}
					})
			);
		}
	}
	
	/**
	 * 固定频率执行任务
	 */
	public static void scheduleAtFixRate(Runnable command, int i, TimeUnit timeUnit) {
		getSeqOrScheduledExecutorService().scheduleAtFixedRate(command, i, i, timeUnit);
	}
}
