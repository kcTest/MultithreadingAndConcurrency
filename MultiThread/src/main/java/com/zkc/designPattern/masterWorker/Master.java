package com.zkc.designPattern.masterWorker;

import com.zkc.util.Print;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Master负责接收客户端提交的任务，然后通过阻塞队列对任务进行缓存。
 * Master所拥有的线程作为阻塞队列的消费者，不断从阻塞队列获取任务并轮流分给Worker
 */
public class Master<T extends Task, R> {
	
	/**
	 * 所有worker集合
	 */
	private final HashMap<String, Worker<T, R>> workers = new HashMap<>();
	/**
	 * 任务集合
	 */
	private final LinkedBlockingQueue<T> taskQueue = new LinkedBlockingQueue<>();
	/**
	 * 任务处理结果集合
	 */
	protected final Map<String, R> resultMap = new ConcurrentHashMap<>();
	
	/**
	 * Master的任务调度线程
	 */
	private Thread thread;
	
	/**
	 * 保存最终的和
	 */
	private final AtomicLong sum = new AtomicLong(0);
	
	public Master(int workerCount) {
		//每个Worker都需要持有queue的引用 用于领任务与提交任务
		for (int i = 0; i < workerCount; i++) {
			Worker<T, R> worker = new Worker<>();
			workers.put("子节点: " + i, worker);
		}
		thread = new Thread(() -> this.execute());
		thread.start();
	}
	
	/**
	 * 提交任务
	 */
	public void submit(T task) {
		taskQueue.add(task);
	}
	
	/**
	 * 启动所有子任务
	 */
	public void execute() {
		for (; ; ) {
			// 轮询Worker节点 轮流分配任务
			for (Map.Entry<String, Worker<T, R>> entry : workers.entrySet()) {
				try {
					//从任务队列中获取任务
					T task = this.taskQueue.take();
					Worker worker = entry.getValue();
					worker.submit(task, this::resultCallback);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * 获取worker处理结果的回调函数
	 */
	private void resultCallback(Object o) {
		Task<R> task = (Task<R>) o;
		String taskName = "Worker:" + task.getWorkerId() + "-" + "Task:" + task.getId();
		R result = task.getResult();
		resultMap.put(taskName, result);
		//和累加
		sum.getAndAdd((Integer) result);
	}
	
	/**
	 * 获取最终结果
	 */
	public void printResult() {
		Print.tco("---------sum is: " + sum.get());
		for (Map.Entry<String, R> entry : resultMap.entrySet()) {
			Print.tco(entry.getKey() + ": " + entry.getValue());
		}
	}
}
