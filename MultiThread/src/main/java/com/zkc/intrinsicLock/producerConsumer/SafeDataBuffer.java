package com.zkc.intrinsicLock.producerConsumer;

import com.zkc.util.Print;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 安全的数据缓存区类
 * 在其add(…)和fetch()两个实例方法的public声明后面加上synchronized关键字即可
 */
public class SafeDataBuffer<T> {
	
	/**
	 * 最大容量
	 */
	public static final int CAPACITY = 10;
	/**
	 * 容器
	 */
	private final List<T> dataList = new LinkedList<>();
	/**
	 * 当前数量
	 */
	private final AtomicInteger amount = new AtomicInteger(0);
	
	/**
	 * 向队列增加一个元素
	 */
	public synchronized void add(T element) throws Exception {
		if (amount.get() > CAPACITY) {
			Print.tcfo("队列已满");
			return;
		}
		dataList.add(element);
		
		Print.tcfo("添加：" + element);
		//数据不一致 抛出异常
		if (amount.incrementAndGet() != dataList.size()) {
			throw new Exception("添加出错," + amount + "!=" + dataList.size());
		}
	}
	
	/**
	 * 从队列取出一个元素
	 */
	public synchronized T fetch() throws Exception {
		if (amount.get() <= 0) {
			Print.tcfo("队列为空");
			return null;
		}
		T element = dataList.remove(0);
		Print.tcfo("取出：" + element);
		//数据不一致 抛出异常
		if (amount.decrementAndGet() != dataList.size()) {
			throw new Exception("移除出错," + amount + "!=" + dataList.size());
		}
		return element;
	}
	
	
}
