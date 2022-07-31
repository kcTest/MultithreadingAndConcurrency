package com.zkc.thread.create;

import com.zkc.util.Print;
import com.zkc.util.ThreadUtil;

import java.util.concurrent.atomic.AtomicInteger;

import static com.zkc.util.ThreadUtil.sleepMilliseconds;

/**
 * “逻辑和数据更好地分离”演示实例
 * <p>
 * <p>
 * 通过对比可以看出：
 * （1）通过继承Thread类实现多线程能更好地做到多个线程并发地完成各自的任务，访问各自的数据资源。
 * （2）通过实现Runnable接口实现多线程能更好地做到多个线程并发地完成同一个任务，访问同一份数据资源。
 * <p>  多个线程的代码逻辑可以方便地访问和处理同一个共享数据资源（如例子中的MallGoods.goodsAmount），
 * <p>  这样可以将线程逻辑和业务数据进行有效的分离，更好地体现了面向对象的设计思想。
 * （3）通过实现Runnable接口实现多线程时，如果数据资源存在多线程共享的情况，那么数据共享资源需要使用原子类型（而不是普通
 * <p>  数据类型），或者需要进行线程的同步控制，以保证对共享数据操作时不会出现线程安全问题。
 * <p>  总之，在大多数情况下，偏向于通过实现Runnable接口来实现线程执行目标类，这样能使代码更加简洁明了。后面介绍线程池的时候
 * <p>  会讲到，异步执行任务在大多数情况下是通过线程池去提交的，而很少通过创建一个新的线程去提交，
 * <p>  所以更多的做法是，通过实现Runnable接口创建异步执行任务，而不是继承Thread去创建异步执行任务
 */
public class SalesDemo {
	public static void main(String[] args) throws InterruptedException {
		Print.cfo("商店版本的销售");
		for (int i = 0; i < 2; i++) {
			Thread thread = new StoreGoods("店员-" + i);
			thread.start();
		}
		
		Thread.sleep(1000);
		
		Print.cfo("商场版本的销售");
		MallGoods mallGoods = new MallGoods();
		for (int i = 0; i < 2; i++) {
			Thread thread = new Thread(mallGoods, "商场销售员-" + i);
			thread.start();
		}
		
		Print.cfo("运行结束");
	}
	
	/**
	 * 商品数量
	 */
	private static final int MAX_AMOUNT = 5;
	
	private static class StoreGoods extends Thread {
		private int goodsAmount = MAX_AMOUNT;
		
		public StoreGoods(String name) {
			super(name);
		}
		
		@Override
		public void run() {
			for (int i = 0; i < MAX_AMOUNT; i++) {
				if (this.goodsAmount > 0) {
					Print.cfo(ThreadUtil.getCurThreadName() + "卖出一件，还剩：" + (--goodsAmount));
					sleepMilliseconds(10);
				}
			}
			Print.cfo(ThreadUtil.getCurThreadName() + "运行结束.");
		}
	}
	
	private static class MallGoods implements Runnable {
		private AtomicInteger goodsAmount = new AtomicInteger(MAX_AMOUNT);
		
		@Override
		public void run() {
			for (int i = 0; i < MAX_AMOUNT; i++) {
				if (this.goodsAmount.get() > 0) {
					Print.cfo(ThreadUtil.getCurThreadName() + "卖出一件，还剩：" + goodsAmount.getAndDecrement());
					sleepMilliseconds(10);
				}
			}
			Print.cfo(ThreadUtil.getCurThreadName() + "运行结束.");
		}
	}
}
