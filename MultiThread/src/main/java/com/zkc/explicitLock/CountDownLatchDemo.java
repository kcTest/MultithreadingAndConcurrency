package com.zkc.explicitLock;

import com.zkc.util.Print;
import com.zkc.util.ThreadUtil;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * （1）创建倒数闩，初始化CountDownLatch时设置倒数的总次数，比如为100。
 * （2）等待线程调用倒数闩的await()方法阻塞自己，等待倒数闩的计数器数值为0（倒数线程全部执行结束）。
 * （3）倒数线程执行完，调用CountDownLatch.countDown()方法将计数器数值减一
 */
public class CountDownLatchDemo {
	
	/**
	 * 司机在开车之前，需要100个乘客并发进行不重复的报数，报数到100之后说明人已经到齐，随后司机可以开车出发
	 */
	public static void main(String[] args) throws InterruptedException {
		//1、创建倒数闩总数
		CountDownLatch latch = new CountDownLatch(N);
		//取得CPU密集型线程池
		ThreadPoolExecutor pool = ThreadUtil.getCPUIntenseTargetThreadPool();
		//启动报数任务
		for (int i = 1; i <= N; i++) {
			pool.execute(new Person(latch, i));
		}
		//2、等待报数完成 倒数闩为0
		latch.await();
		Print.tcfo("人到齐，开车");
	}
	
	//乘客数
	private static final int N = 100;
	
	private static class Person implements Runnable {
		
		private final CountDownLatch latch;
		private final int i;
		
		Person(CountDownLatch latch, int i) {
			this.latch = latch;
			this.i = i;
		}
		
		@Override
		public void run() {
			try {
				//报数
				Print.tcfo("第" + i + "个人到了");
				//3、倒数闩减少1
				latch.countDown();
			} catch (Exception ignored) {
			}
		}
	}
}
