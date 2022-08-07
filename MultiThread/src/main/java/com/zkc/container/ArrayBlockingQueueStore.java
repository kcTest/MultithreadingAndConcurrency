package com.zkc.container;

import com.zkc.petStore.action.Consumer;
import com.zkc.petStore.action.Producer;
import com.zkc.petStore.goods.Goods;
import com.zkc.petStore.goods.IGoods;
import com.zkc.util.JvmUtil;
import com.zkc.util.Print;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * ArrayBlockingQueue中的元素访问存在公平访问与非公平访问两种方式，所以ArrayBlockingQueue可以分别作为公平队列和非公平队列使用：
 * （1）对于公平队列，被阻塞的线程可以按照阻塞的先后顺序访问队列，即先阻塞的线程先访问队列。
 * （2）对于非公平队列，当队列可用时，阻塞的线程将进入争夺访问资源的竞争中，也就是说谁先抢到谁就执行，没有固定的先后顺序
 */
public class ArrayBlockingQueueStore {
	
	public static void main(String[] args) {
		Print.cfo("当前线程的ID是：" + JvmUtil.getProcessId());
		System.setErr(System.out);
		
		//共享数据区 实例对象
		DataBuffer<IGoods> dataBuffer = new DataBuffer<>();
		
		//生产者行为
		Callable<IGoods> produceAction = () -> {
			//首先生产随机商品
			IGoods goods = Goods.produceOne();
			//将商品加入共享数据区
			dataBuffer.add(goods);
			return goods;
		};
		//消费者行为
		//从商店中获取商品
		Callable<IGoods> consumerAction = dataBuffer::fetch;
		
		//同时并发执行的线程数
		final int threads = 20;
		//线程池 多线程模拟测试
		ExecutorService pool = Executors.newFixedThreadPool(threads);
		// 1个生产者 10个消费者
		final int PRODUCER_COUNT = 1, CONSUMER_COUNT = 10;
		for (int i = 0; i < PRODUCER_COUNT; i++) {
			// 每生产一个商品 间隔50ms
			pool.submit(new Producer(produceAction, 50));
		}
		for (int i = 0; i < CONSUMER_COUNT; i++) {
			//每消耗一个商品 间隔100ms
			pool.submit(new Consumer(consumerAction, 100));
		}
	}
	
	private static final int CAPACITY = 10;
	
	//共享数据区
	private static class DataBuffer<T> {
		
		//使用阻塞队列保存数据
		private final ArrayBlockingQueue<T> dataList = new ArrayBlockingQueue<>(CAPACITY);
		
		/**
		 * 向数据区增加一个元素 委托给阻塞队列
		 */
		public void add(T element) throws Exception {
			dataList.add(element);
		}
		
		/**
		 * 从数据区取出一个商品 委托给阻塞队列
		 */
		public T fetch() throws Exception {
			return dataList.take();
		}
	}
	
}
