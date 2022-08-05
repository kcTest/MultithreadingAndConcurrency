package com.zkc.intrinsicLock.producerConsumer;

import com.zkc.petStore.action.Consumer;
import com.zkc.petStore.action.Producer;
import com.zkc.petStore.goods.Goods;
import com.zkc.petStore.goods.IGoods;
import com.zkc.util.JvmUtil;
import com.zkc.util.Print;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ReentrantLockStoreDemo {
	
	public static void main(String[] args) {
		Print.cfo("当前进程ID：" + JvmUtil.getProcessId());
		System.setErr(System.out);
		
		//共享数据区
		DataBuffer<IGoods> dataBuffer = new DataBuffer<>();
		
		//生产者执行的动作
		Callable<IGoods> produceAction = () -> {
			//生成随机类型商品
			IGoods goods = Goods.produceOne();
			//加入共享数据区
			dataBuffer.add(goods);
			return goods;
		};
		
		//消费者执行的动作
		Callable<IGoods> consumerAction = () -> {
			//获取商品
			return dataBuffer.fetch();
		};
		
		//同时并发执行的线程数
		final int threadTotal = 20;
		//线程池 多线程模拟测试
		ExecutorService pool = Executors.newFixedThreadPool(threadTotal);
		
		//设置消费者线程10个 生产者线程1个
		final int producerTotal = 1, consumerTotal = 10;
		for (int i = 0; i < producerTotal; i++) {
			//生产者线程每50ms生产一个商品
			pool.submit(new Producer(produceAction, 50));
		}
		for (int i = 0; i < consumerTotal; i++) {
			//消费者线程每100ms消耗一个商品
			pool.submit(new Consumer(consumerAction, 100));
		}
		
	}
	
	private static final int CAPACITY = 10;
	
	static class DataBuffer<T> {
		private final List<T> dataList = new LinkedList<>();
		private int amount = 0;
		private static final Lock LOCK_OBJECT = new ReentrantLock();
		private static final Condition NOT_FULL = LOCK_OBJECT.newCondition();
		private static final Condition NOT_EMPTY = LOCK_OBJECT.newCondition();
		
		//向数据区增加元素
		public void add(T element) throws Exception {
			//避免空轮询导致CPU时间片浪费
			while (amount > CAPACITY) {
				LOCK_OBJECT.lock();
				try {
					Print.tcfo("队列已满 无法添加");
					//等待未满通知
					NOT_FULL.await();
				} finally {
					LOCK_OBJECT.unlock();
				}
			}
			LOCK_OBJECT.lock();
			try {
				dataList.add(element);
				amount++;
				NOT_EMPTY.signal();
			} finally {
				LOCK_OBJECT.unlock();
			}
		}
		
		//从数据区中取元素
		public T fetch() throws Exception {
			//避免空轮询导致CPU时间片浪费
			while (amount <= 0) {
				LOCK_OBJECT.lock();
				try {
					Print.tcfo("队列已空 无法读取");
					//等待非空通知
					NOT_EMPTY.await();
				} finally {
					LOCK_OBJECT.unlock();
				}
			}
			T element;
			LOCK_OBJECT.lock();
			try {
				element = dataList.remove(0);
				amount--;
				NOT_FULL.signal();
			} finally {
				LOCK_OBJECT.unlock();
			}
			return element;
		}
	}
}
