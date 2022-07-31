package com.zkc.threadPool.create;

import com.zkc.util.Print;
import com.zkc.util.ThreadUtil;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 可调度线程池”的适用场景：周期性地执行任务的场景
 */
public class createDemo04 {
	public static void main(String[] args) {
		ScheduledExecutorService scheduledPool = Executors.newScheduledThreadPool(2);
		for (int i = 0; i < MAX; i++) {
			//initialDelay, //首次执行延时;  period, //两次开始执行最小间隔时间
//			scheduledPool.scheduleAtFixedRate(new TargetTask(), 0, 500, TimeUnit.MILLISECONDS);
			//initialDelay, //首次执行延时;  delay, /前一次执行结束 到 下一次执行开始 的间隔时间（间隔执行的延迟时间）
			scheduledPool.scheduleWithFixedDelay(new TargetTask(), 0, 500, TimeUnit.MILLISECONDS);
			/*
			当被调任务的执行时间大于指定的间隔时间时，ScheduleExecutorService并不会创建一个新的线程去并发执行这个任务，而是等待前一次调度执行完毕
			*/
		}
		ThreadUtil.sleepSeconds(1000);
		scheduledPool.shutdown();
	}
	
	private static final int SLEEP_GAP = 500;
	private static final int MAX = 2;
	
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
