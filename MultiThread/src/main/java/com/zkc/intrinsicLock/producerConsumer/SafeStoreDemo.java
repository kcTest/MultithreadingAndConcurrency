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
public class SafeStoreDemo {
	
	public static void main(String[] args) {
		System.setErr(System.out);
		//同时并发执行的线程数
		final int nThreads = 20;
		//线程池，用于多线程模拟测试
		ExecutorService threadPool = Executors.newFixedThreadPool(nThreads);
		for (int i = 0; i < 5; i++) {
			//生产者每隔500ms生产一个产品
			threadPool.submit(new Producer(PRODUCE_ACTION), 500);
			//消费者每隔1500ms消费一个产品
			threadPool.submit(new Consumer(CONSUME_ACTION), 1500);
		}
		//使用SafeDataBuffer实例的对象锁作为同步锁，这样一来，所有的生产、消费动作在执行过程中都需要抢占同一个同步锁，最终的结果是所有
		//的生产、消费动作都被串行化了。高效率的生产者-消费者模式，生产、消费动作肯定不能串行执行，而是需要并行执行
	}
	
	/**
	 * 数据缓冲区
	 */
	private static final SafeDataBuffer<IGoods> NOT_SAFE_DATA_BUFFER = new SafeDataBuffer<>();
	
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
