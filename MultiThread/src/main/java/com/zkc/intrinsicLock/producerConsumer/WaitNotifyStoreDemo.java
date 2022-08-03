package com.zkc.intrinsicLock.producerConsumer;

import com.zkc.petStore.action.Consumer;
import com.zkc.petStore.action.Producer;
import com.zkc.petStore.goods.Goods;
import com.zkc.petStore.goods.IGoods;
import com.zkc.util.JvmUtil;
import com.zkc.util.Print;

import java.lang.management.ManagementFactory;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WaitNotifyStoreDemo {
	
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
		private static final Object LOCK_OBJECT = new Object();
		private static final Object NOT_FULL = new Object();
		private static final Object NOT_EMPTY = new Object();
		
		//向数据区增加元素
		public void add(T element) throws Exception {
			//避免空轮询导致CPU时间片浪费
			while (amount > CAPACITY) {
				synchronized (NOT_FULL) {
					Print.tcfo("队列已满 无法添加");
					//等待未满通知
					NOT_FULL.wait();
				}
			}
			synchronized (LOCK_OBJECT) {
				dataList.add(element);
				amount++;
			}
			synchronized (NOT_EMPTY) {
				//发送非空通知
				NOT_EMPTY.notify();
			}
		}
		
		//从数据区中取元素
		public T fetch() throws Exception {
			//避免空轮询导致CPU时间片浪费
			while (amount <= 0) {
				synchronized (NOT_EMPTY) {
					Print.tcfo("队列已空 无法读取");
					//等待非空通知
					NOT_EMPTY.wait();
				}
			}
			T element;
			synchronized (LOCK_OBJECT) {
				element = dataList.remove(0);
				amount--;
			}
			synchronized (NOT_FULL) {
				//发送未满通知
				NOT_FULL.notify();
			}
			return element;
		}
	}
}
