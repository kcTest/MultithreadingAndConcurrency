package com.zkc.thread.create;

import com.zkc.util.Print;
import com.zkc.util.ThreadUtil;

import java.util.concurrent.*;

/**
 * 创建一个线程实例在时间成本、资源耗费上都很高（稍后会介绍），在高并发的场景中，断然不
 * 能频繁进行线程实例的创建与销毁，而是需要对已经创建好的线程实例进行复用，这就涉及线程池的技术.
 * <p>
 * 使用Executors创建线程池，然后使用ExecutorService线程池执行或者提交target执行目标实例
 */
public class CreateDemo04 {
	
	public static void main(String[] args) throws ExecutionException, InterruptedException {
		pool.execute(new DemoThread());
		pool.execute(new Runnable() {
			@Override
			public void run() {
				for (int i = 0; i < MAX; i++) {
					Print.cfo(ThreadUtil.getCurThreadName() + ", 轮次：" + i);
					ThreadUtil.sleepMilliseconds(10);
				}
			}
		});
		Future<Long> future = pool.submit(new ReturnableTask());
		Long ret = future.get();
		Print.cfo("异步任务执行结果为: " + ret);
		ThreadUtil.sleepMilliseconds(Integer.MAX_VALUE);
	}
	
	public static final int MAX = 5;
	/**
	 * ExecutorService实例负责对池中的线程进行管理和调度，并且可以有效控制最大并发线程数，提高系统资源的使用率，
	 * 同时提供定时执行、定频执行、单线程、并发数控制等功能
	 * <p>
	 * 创建包含固定数目的线程池，示例中的线程数量为3
	 */
	private static ExecutorService pool = Executors.newFixedThreadPool(3);
	
	
	private static class DemoThread extends Thread {
		
		@Override
		public void run() {
			for (int i = 0; i < MAX; i++) {
				Print.cfo(ThreadUtil.getCurThreadName() + "， 轮次：" + i);
				ThreadUtil.sleepMilliseconds(10);
			}
		}
	}
	
	private static class ReturnableTask implements Callable<Long> {
		
		@Override
		public Long call() throws Exception {
			long startTime = System.currentTimeMillis();
			Print.cfo(ThreadUtil.getCurThreadName() + " 线程运行开始.");
			for (int i = 0; i < MAX; i++) {
				Print.cfo(ThreadUtil.getCurThreadName() + ", 轮次：" + i);
				ThreadUtil.sleepMilliseconds(10);
			}
			long used = System.currentTimeMillis() - startTime;
			Print.cfo(ThreadUtil.getCurThreadName() + " 线程运行结束.");
			return used;
		}
	}
	
}
