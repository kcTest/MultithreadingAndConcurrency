package com.zkc.petStore.action;

import com.zkc.util.Print;
import com.zkc.util.ThreadUtil;

import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 生产者定义
 * Producer类组合了一个Callable类型的成员action实例，action代表了生产数据所需要执行的实际动作
 */
public class Producer implements Runnable {
	
	/**
	 * 默认生产时间间隔 200ms
	 */
	private static final int DEFAULT_PRODUCE_GAP = 200;
	
	/**
	 * 生产次数
	 */
	private static final AtomicInteger PRODUCE_COUNT = new AtomicInteger(0);
	
	/**
	 * 生产者编号
	 */
	private static final AtomicInteger PRODUCER_NO = new AtomicInteger(0);
	
	/**
	 * 生产者名称
	 */
	private String producerName;
	
	/**
	 * 生产动作
	 */
	private Callable<?> action;
	
	private int gap;
	
	public Producer(Callable<?> action) {
		this.action = action;
		this.gap = DEFAULT_PRODUCE_GAP;
		this.producerName = "生产者-" + PRODUCER_NO.incrementAndGet();
	}
	
	
	public Producer(Callable<?> action, int gap) {
		this.action = action;
		this.gap = gap;
		if (this.gap <= 0) {
			this.gap = DEFAULT_PRODUCE_GAP;
		}
		this.producerName = "生产者-" + PRODUCER_NO.incrementAndGet();
	}
	
	@Override
	public void run() {
		while (true) {
			try {
				//执行生产者动作
				Object out = action.call();
				if (out != null) {
					//打印生产结果  增加生产次数
					Print.tcfo(String.format("%s 执行第%d轮生产:%s", this.producerName, PRODUCE_COUNT.incrementAndGet(), out));
				}
				//每一轮生产之后 等待一下
				ThreadUtil.sleepMilliseconds(gap);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
