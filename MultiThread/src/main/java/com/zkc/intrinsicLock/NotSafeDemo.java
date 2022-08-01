package com.zkc.intrinsicLock;

import com.zkc.util.Print;

import java.util.concurrent.CountDownLatch;


/**
 * 10个线程并行运行，对一个共享数据进行自增运算，每个线程自增运算1000次
 * 自增运算符“++”不是线程安全的
 */
public class NotSafeDemo {
	
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
		
		public void selfPlus() {
			amount++;
		}
		
		public int getAmount() {
			return amount;
		}
	}
}
