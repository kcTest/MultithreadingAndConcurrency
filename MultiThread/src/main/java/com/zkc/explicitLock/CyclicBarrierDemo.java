package com.zkc.explicitLock;

import java.util.concurrent.CyclicBarrier;

/**
 * 可循环使用（Cyclic）的屏障（Barrier）。
 * 让一组线程到达一个屏障（也可以叫同步点）时被阻塞，直到最后一个线程到达屏障时，屏障才会开门，所有被屏障拦截的线程才会继续干活
 */
public class CyclicBarrierDemo {
	
	public static void main(String[] args) {
		int threadNum = 5;
		
		//parties 代表有拦截的线程的数量，当拦截的线程数量达到这个值的时候就打开栅栏，让所有线程通过。
		// barrierAction 最后一个到达线程要做的任务
		CyclicBarrier cyclicBarrier = new CyclicBarrier(threadNum, new Runnable() {
			@Override
			public void run() {
				System.out.println(Thread.currentThread().getName() + " 完成最后任务");
			}
		});
		
		for (int i = 0; i < threadNum; i++) {
			new TaskThread(cyclicBarrier).start();
		}
		
	}
	
	private static class TaskThread extends Thread {
		
		private final CyclicBarrier cyclicBarrier;
		
		public TaskThread(CyclicBarrier cyclicBarrier) {
			this.cyclicBarrier = cyclicBarrier;
		}
		
		@Override
		public void run() {
			
			try {
				Thread.sleep(1000);
				System.out.println(getName() + " 到达栅栏 A");
				//await() 方法将线程挡住 当拦住的线程数量达到 parties 的值时，栅栏才会打开，线程才得以通过执行。
				cyclicBarrier.await();
				System.out.println(getName() + " 冲破栅栏 A");
				
				Thread.sleep(2000);
				System.out.println(getName() + " 到达栅栏 B");
				cyclicBarrier.await();
				System.out.println(getName() + " 冲破栅栏 B");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}
	
	
}
