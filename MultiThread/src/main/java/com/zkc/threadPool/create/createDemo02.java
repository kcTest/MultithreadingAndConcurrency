package com.zkc.threadPool.create;

import com.zkc.util.Print;
import com.zkc.util.ThreadUtil;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * （1）如果线程数没有达到“固定数量”，每次提交一个任务线程池内就创建一个新线程，直到线程达到线程池固定的数量。
 * （2）线程池的大小一旦达到“固定数量”就会保持不变，如果某个线程因为执行异常而结束，那么线程池会补充一个新线程。
 * （3）在接收异步任务的执行目标实例时，如果池中的所有线程均在繁忙状态，新任务会进入阻塞队列中（无界的阻塞队列）。
 * <p>   “固定数量的线程池”的适用场景：
 * <p>      需要任务长期执行的场景。“固定数量的线程池”的线程数能够比较稳定地保证一个数，能够避免频繁回收线程和创建线程，
 * <p>      故适用于处理CPU密集型的任务，在CPU被工作线程长时间占用的情况下，能确保尽可能少地分配线程。
 * <p>   “固定数量的线程池”的弊端：
 * <p>      内部使用无界队列来存放排队任务，当大量任务超过线程池最大容量需要处理时，队列无限增大，使服务器资源迅速耗尽
 */
public class createDemo02 {
	public static void main(String[] args) {
		ExecutorService pool = Executors.newFixedThreadPool(3);
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
