package com.zkc.intrinsicLock;

import com.zkc.petStore.action.Consumer;
import com.zkc.petStore.action.Producer;
import com.zkc.petStore.goods.Goods;
import com.zkc.petStore.goods.IGoods;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NotSafeStoreDemo01 {
	
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
	}
	
	/**
	 * 共享数据
	 */
	private static final NotSafeDataBuffer<IGoods> NOT_SAFE_DATA_BUFFER = new NotSafeDataBuffer<>();
	
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
