package com.zkc.basic;

import com.zkc.util.Print;
import com.zkc.util.ThreadUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * （1）yield仅能使一个线程从运行状态转到就绪状态，而不是阻塞状态。
 * （2）yield不能保证使得当前正在运行的线程迅速转换到就绪状态。
 * （3）即使完成了迅速切换，系统通过线程调度机制从所有就绪线程中挑选下一个执行线程时，
 * 就绪的线程有可能被选中，也有可能不被选中，其调度的过程受到其他因素（如优先级）的影响
 */
public class YieldDemo {
	
	public static void main(String[] args) {
		Thread thread1 = new YieldThread();
		//设置最高优先级
		thread1.setPriority(Thread.MAX_PRIORITY);
		Thread thread2 = new YieldThread();
		//设置最低优先级
		thread2.setPriority(Thread.MIN_PRIORITY);
		Print.tfo("启动线程");
		/*从输出的结果可以看出，优先级高的YieldThread-1执行的次数比优先级低的YieldThread-2执行的次数多很多。
		得到的结论是：线程调用yield之后，操作系统在重新进行线程调度时偏向于将执行机会让给优先级较高的线程*/
		thread1.start();
		thread2.start();
		ThreadUtil.sleepSeconds(100);
	}
	
	private static final int MAX = 100;
	private static AtomicInteger index = new AtomicInteger(0);
	private static Map<String, AtomicInteger> countMap = new HashMap<>();
	
	private static void printCountMap() {
		Print.tfo("countMap= " + countMap);
	}
	
	private static class YieldThread extends Thread {
		private static int threadNo = 1;
		
		public YieldThread() {
			super("YieldThread-" + threadNo++);
			countMap.put(this.getName(), new AtomicInteger(1));
		}
		
		@Override
		public void run() {
			for (int i = 0; i < MAX && index.get() < MAX; i++) {
				Print.tfo("线程优先级：" + getPriority());
				index.incrementAndGet();
				//统计
				countMap.get(this.getName()).incrementAndGet();
				if ((i & 1) != 0) {
					//让出执行权限
					Thread.yield();
				}
			}
			//输出所有线程的执行次数
			printCountMap();
			Print.tfo(getName() + " 运行结束.");
		}
	}
}
