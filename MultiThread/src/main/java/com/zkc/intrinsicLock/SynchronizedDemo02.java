package com.zkc.intrinsicLock;

import com.zkc.util.Print;

import java.util.concurrent.CountDownLatch;

/**
 * synchronized同步块
 * <p>
 * 为了提升吞吐量，可以将synchronized关键字放在函数体内
 * <p>
 * 将syncObject对象监视锁作为临界区代码段的同步锁
 * 由于同步块1和同步块2保护着两个独立的临界区代码段，需要两把不同的syncObject对象锁
 */
public class SynchronizedDemo02 {
	
	public static void main(String[] args) throws InterruptedException {
		CountDownLatch latch = new CountDownLatch(LOOP);
		TwoPlus counter = new TwoPlus();
		Runnable targetTask = () -> {
			for (int i = 0; i < PLUS; i++) {
				counter.plus(1, 1);
			}
			latch.countDown();
		};
		for (int i = 0; i < LOOP; i++) {
			new Thread(targetTask).start();
		}
		latch.await();
		Print.tcfo("理论结果: " + 2 * LOOP * PLUS);
		Print.tcfo("实际结果：" + counter.getAmount());
		Print.tcfo("差值：" + (2 * LOOP * PLUS - counter.getAmount()));
	}
	
	private static final int LOOP = 10;
	private static final int PLUS = 1000;
	
	private static class TwoPlus {
		private int sum1 = 0;
		private int sum2 = 0;
		//同步锁1
		private final Integer sum1Lock = 1;
		//同步锁2
		private final Integer sum2Lock = 2;
		
		/**
		 * 两个临界区资源分别为sum1和sum2。使用synchronized对plus(intval1,int val2)进行同步保护之后，进入临界区代码段的线程拥有
		 * sum1和sum2的操作权，并且是全部占用。一旦线程进入，当线程在操作sum1而没有操作sum2时，也将sum2的操作权白白占用，其他的线程
		 * 由于没有进入临界区，只能看着sum2被闲置而不能去执行操作
		 */
		public void plus(int val1, int val2) {
			synchronized (this.sum1Lock) {
				this.sum1 += val1;
			}
			synchronized (this.sum2Lock) {
				this.sum2 += val2;
			}
		}
		
		public int getAmount() {
			return this.sum1 + this.sum2;
		}
	}
}

