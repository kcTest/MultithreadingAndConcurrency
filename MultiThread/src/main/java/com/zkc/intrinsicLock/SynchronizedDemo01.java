package com.zkc.intrinsicLock;

import com.zkc.util.Print;

import java.util.concurrent.CountDownLatch;

/**
 * 使用synchronized（syncObject）调用相当于获取syncObject的内置锁，所以可以使用内置锁对临界区代码段进行排他性保护
 * <p>
 * synchronized同步方法
 */
public class SynchronizedDemo01 {
	
	public static void main(String[] args) throws InterruptedException {
		CountDownLatch latch = new CountDownLatch(LOOP);
		NotSafePlus counter = new NotSafePlus();
		Runnable targetTask = () -> {
			for (int i = 0; i < PLUS; i++) {
				counter.selfPlus();
			}
			latch.countDown();
		};
		for (int i = 0; i < LOOP; i++) {
			new Thread(targetTask).start();
		}
		/* 等到latch实例倒数到0才能继续执行 */
		latch.await();
		Print.tcfo("理论结果: " + LOOP * PLUS);
		Print.tcfo("实际结果：" + counter.getAmount());
		Print.tcfo("差值：" + (LOOP * PLUS - counter.getAmount()));
	}
	
	private static final int LOOP = 10;
	private static final int PLUS = 1000;
	
	private static class NotSafePlus {
		private int amount = 0;
		
		/**
		 * 使用synchronized关键字对临界区代码段进行保护  该方法被声明为同步方法
		 * <p>
		 * 使用synchronized方法对方法内部全部代码进行保护
		 */
		public synchronized void selfPlus() {
			amount++;
		}
		
		/**
		 * 使用synchronized代码块对方法内部全部代码进行保护
		 * <p>
		 * 两种实现多线程同步的plus方法版本编译成JVM内部字节码后结果是一样的
		 */
//		public void selfPlus2() {
//			synchronized (this) {
//				amount++;
//			}
//		}
		
		public int getAmount() {
			return amount;
		}
	}
}

