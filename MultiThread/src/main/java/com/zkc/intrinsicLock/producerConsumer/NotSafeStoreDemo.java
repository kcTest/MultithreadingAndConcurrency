package com.zkc.intrinsicLock.producerConsumer;

import com.zkc.petStore.action.Consumer;
import com.zkc.petStore.action.Producer;
import com.zkc.petStore.goods.Goods;
import com.zkc.petStore.goods.IGoods;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 生产者和消费者模式的简单实现版本
 */
public class NotSafeStoreDemo {
	
	public static void main(String[] args) {
		System.setErr(System.out);
		//同时并发执行的线程数
		final int THREAD_TOTAL = 20;
		//线程池，用于多线程模拟测试
		ExecutorService threadPool = Executors.newFixedThreadPool(THREAD_TOTAL);
		for (int i = 0; i < 5; i++) {
			//生产者每隔500ms生产一个产品
			threadPool.submit(new Producer(PRODUCE_ACTION), 500);
			//消费者每隔1500ms消费一个产品
			threadPool.submit(new Consumer(CONSUME_ACTION), 1500);
		}
		//在向数据缓冲区进行元素的增加或者提取时，多个线程在并发执行对amount、dataList两个成员操作时次序已
		//经混乱，导致出现数据不一致和线程安全问题
	}
	
	/**
	 * 数据缓冲区
	 */
	private static final NotSafeDataBuffer<IGoods> NOT_SAFE_DATA_BUFFER = new NotSafeDataBuffer<>();
	
	/**
	 * 生产者动作
	 */
	private static final Callable<IGoods> PRODUCE_ACTION = () -> {
		//生成随机类型商品
		IGoods goods = Goods.produceOne();
		try {
			NOT_SAFE_DATA_BUFFER.add(goods);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return goods;
	};
	
	/**
	 * 消费者动作
	 */
	private static final Callable<IGoods> CONSUME_ACTION = () -> {
		//从PetStore获取商品
		IGoods goods = null;
		try {
			goods = NOT_SAFE_DATA_BUFFER.fetch();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return goods;
	};
	
}
