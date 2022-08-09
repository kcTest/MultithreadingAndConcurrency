package com.zkc.petStore.action;

import com.zkc.util.Print;
import com.zkc.util.ThreadUtil;

import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 消费者定义
 * Consumer类组合了一个Callable类型的成员action实例，action代表了消费者所需要执行的实际消耗动作
 */
public class Consumer implements Runnable {
	
	/**
	 * 默认消费时间间隔 100ms
	 */
	private static final int DEFAULT_PRODUCE_GAP = 200;
	
	/**
	 * 消费次数
	 */
	private static final AtomicInteger PRODUCE_COUNT = new AtomicInteger(0);
	
	/**
	 * 消费者编号
	 */
	private static final AtomicInteger CONSUMER_NO = new AtomicInteger(0);
	
	/**
	 * 消费者名称
	 */
	private String consumerName;
	
	/**
	 * 消费动作
	 */
	private Callable<?> action;
	
	private int gap;
	
	public Consumer(Callable<?> action) {
		this.action = action;
		this.gap = DEFAULT_PRODUCE_GAP;
		this.consumerName = "消费者-" + CONSUMER_NO.incrementAndGet();
	}
	
	
	public Consumer(Callable<?> action, int gap) {
		this.action = action;
		this.gap = gap;
		if (this.gap <= 0) {
			this.gap = DEFAULT_PRODUCE_GAP;
		}
		this.consumerName = "消费者-" + CONSUMER_NO.incrementAndGet();
	}
	
	@Override
	public void run() {
		while (true) {
			try {
				//执行消费者动作
				Object out = action.call();
				if (out != null) {
					//打印消费结果  增加消费次数
					Print.tcfo(String.format("%s 执行第%d轮消费:%s", this.consumerName, PRODUCE_COUNT.incrementAndGet(), out));
				}
				//每一轮消费之后 等待一下
				ThreadUtil.sleepMilliseconds(gap);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
