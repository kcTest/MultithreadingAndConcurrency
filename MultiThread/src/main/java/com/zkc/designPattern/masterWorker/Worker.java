package com.zkc.designPattern.masterWorker;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

/**
 * Worker接收Master分配的任务，同样也通过阻塞队列对局部任务进行缓存。
 * Worker所拥有的线程作为局部任务的阻塞队列的消费者，不断从阻塞队列获取任务并执行，执行完成后回调Master传递过来的回调函数
 */
public class Worker<T extends Task, R> {
	
	/**
	 * 接受任务的阻塞队列
	 */
	private final LinkedBlockingQueue<T> taskQueue = new LinkedBlockingQueue<>();
	/**
	 * Worker的编号
	 */
	private static final AtomicInteger INDEX = new AtomicInteger(1);
	private final int workId;
	/**
	 * 执行任务的线程
	 */
	private Thread thread;
	
	public Worker() {
		this.workId = INDEX.getAndIncrement();
		thread = new Thread(() -> this.run());
		thread.start();
	}
	
	/**
	 * 接受任务到队列
	 */
	public void submit(T task, Consumer<R> action) {
		//设置任务的回调方法
		task.resultAction = action;
		try {
			this.taskQueue.put(task);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 轮询执行任务
	 */
	public void run() {
		//轮询所有的子任务
		for (; ; ) {
			try {
				//从阻塞队列中提取任务
				T task = this.taskQueue.take();
				task.setWorkerId(workId);
				task.execute();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
}
