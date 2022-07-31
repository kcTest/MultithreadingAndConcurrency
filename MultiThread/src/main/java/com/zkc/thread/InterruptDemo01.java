package com.zkc.thread;

import com.zkc.util.Print;
import com.zkc.util.ThreadUtil;

/**
 * 强行中断线程可能导致数据不一致的问题。正是由于调用stop()方法来终止线程可能会产生不可预料的结果，因此不推荐调用stop()方法
 * <p>
 * 调用线程的interrupt()方法时，它有两个作用：
 * （1）如果此线程处于阻塞状态（如调用了Object.wait()方法），就会立马退出阻塞，并抛出InterruptedException异常，
 * <p> 线程就可以通过捕获InterruptedException来做一定的处理，然后让线程退出。
 * <p> 更确切地说，如果线程被Object.wait()、Thread.join()和Thread.sleep()三种方法之一阻塞，此时调用该线程的interrupt()方
 * <p> 法，该线程将抛出一个InterruptedException中断异常（该线程必须事先预备好处理此异常），从而提早终结被阻塞状态。
 * （2）如果此线程正处于运行之中，线程就不受任何影响，继续运行，仅仅是线程的中断标记被设置为true。所以，程序可以在适当的
 * <p> 位置通过调用isInterrupted()方法来查看自己是否被中断，并执行退出操作
 * <p>
 * 如果线程的interrupt()方法先被调用，然后线程开始调用阻塞方法进入阻塞状态，
 * InterruptedException异常依旧会抛出。如果线程捕获InterruptedException异常后，
 * 继续调用阻塞方法，将不再触发InterruptedException异常。
 * <p>
 * Thread.interrupt()方法 只是改变中断状态，不会中断一个正在运行的线程，线程是否停止执
 * 行，需要用户程序去监视线程的isInterrupted()状态，并进行相应的处理。
 */
public class InterruptDemo01 {
	
	public static void main(String[] args) {
		Thread thread1 = new SleepThread();
		thread1.start();
		Thread thread2 = new SleepThread();
		thread2.start();
		
		ThreadUtil.sleepSeconds(2);
		//1线程正在睡眠  会抛出异常
		thread1.interrupt();
		
		ThreadUtil.sleepSeconds(5);
		//2线程已经执行结束 不受影响
		thread2.interrupt();
		
		ThreadUtil.sleepSeconds(1);
		
		Print.cfo("程序运行结束.");
	}
	
	public static final int SLEEP_GAP = 5000;
	
	private static class SleepThread extends Thread {
		
		private static int threadSeqNumber = 1;
		
		public SleepThread() {
			super("sleepThread-" + threadSeqNumber++);
		}
		
		@Override
		public void run() {
			try {
				Print.cfo(getName() + " 进入睡眠.");
				Thread.sleep(SLEEP_GAP);
			} catch (InterruptedException e) {
				e.printStackTrace();
				Print.cfo(getName() + " 发生异常被打断.");
				return;
			}
			Print.cfo(getName() + " 运行结束.");
		}
	}
	
}
