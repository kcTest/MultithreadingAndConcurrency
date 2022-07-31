package com.zkc.thread;

import com.zkc.util.Print;
import com.zkc.util.ThreadUtil;

import java.util.HashSet;
import java.util.Set;

/**
 * NEW, //新建  创建成功但是没有调用start()方法启动的Thread线程实例都处于NEW状态
 * RUNNABLE, //可执行：包含操作系统的就绪、运行两种状态。处于就绪状态的线程需要等待系统的调度，一旦就绪状态被系统选中，
 * <p>               获得CPU时间片，线程就开始占用CPU，开始执行线程的代码，这时线程的操作系统状态发生了改变，进入了运行状态
 * <p>               Java语言中，并没有细分这两种状态，而是将这两种状态合并成同一种状态 RUNNABLE状态
 * BLOCKED, //阻塞 处于BLOCKED（阻塞）状态的线程并不会占用CPU资源，以下情况会让线程进入阻塞状态：
 * <p>     （1）线程等待获取锁
 * <p>          等待获取一个锁，而该锁被其他线程持有，则该线程进入阻塞状态。当其他线程释放了该锁，并且线程调度器允许该线程持有该锁时，该线程退出阻塞状态。
 * <p>     （2）IO阻塞
 * <p>          线程发起了一个阻塞式IO操作后，如果不具备IO操作的条件，线程就会进入阻塞状态。
 * <p>          IO包括磁盘IO、网络IO等。IO阻塞的一个简单例子：线程等待用户输入内容后继续执行
 * WAITING, //等待
 * <p>         线程的WAITING（等待）状态表示线程在等待被唤醒。处于WAITING状态的线程不会被分配CPU时间片。
 * <p>         执行以下两个操作，当前线程将处于WAITING状态：
 * <p>        （1）执行没有时限（timeout）参数的thread.join()调用：在线程合并场景中，若线程A调用B.join()去合入B线程，则在B执行期间线
 * <p>           程A处于WAITING状态，一直等线程B执行完成。
 * <p>        （2）执行没有时限（timeout）参数的object.wait()调用：指一个拥有object对象锁的线程，进入相应的代码临界区后，调用相应的
 * <p>           object的wait()方法去等待其“对象锁”（Object Monitor）上的信号，若“对象锁”上没有信号，则当前线程处于WAITING状态
 * <p>            对应的唤醒方式为：Object.notify()/Object.notifyAll()
 * <p>        （3） LockSupport.park()方法，对应的唤醒方式为：LockSupport.unpark(Thread)
 * TIMED_WAITING,//限时等待
 * <p>             线程不会被分配CPU时间片，它们要等待被唤醒，或者直到等待的时限到期。能让线程处于限时等待状态的操作大致有以下几种：
 * <p>             （1）Thread.sleep(int n)：使得当前线程进入限时等待状态，等待时间为n毫秒。
 * <p>             （2）Object.wait()：带时限的抢占对象的monitor锁。（3）Thread.join()：带时限的线程合并。
 * <p>             （4）LockSupport.parkNanos()：让线程等待，时间以纳秒为单位。
 * <p>             （5）LockSupport.parkUntil()：让线程等待，时间可以灵活设置。
 * TERMINATED; //终止  run()方法执行完成、run()方法被异常终止
 * <p>
 * 演示线程状态
 */
public class StatusDemo {
	
	public static void main(String[] args) {
		addStatusThread(Thread.currentThread());
		
		Thread thread1 = new StatusThread();
		Print.cfo(thread1.getName() + ", 状态为" + thread1.getState());
		Thread thread2 = new StatusThread();
		Print.cfo(thread2.getName() + ", 状态为" + thread2.getState());
		Thread thread3 = new StatusThread();
		Print.cfo(thread3.getName() + ", 状态为" + thread3.getState());
		
		thread1.start();
		ThreadUtil.sleepMilliseconds(500);
		
		thread2.start();
		ThreadUtil.sleepMilliseconds(500);
		
		thread3.start();
		ThreadUtil.sleepMilliseconds(100);
	}
	
	private static final int MAX = 5;
	private static int threadSeqNumber = 0;
	private static Set<Thread> threadList = new HashSet<>(3);
	
	private static void addStatusThread(Thread thread) {
		threadList.add(thread);
	}
	
	private static void printTheadStatus() {
		for (Thread thread : threadList) {
			Print.cfo(thread.getName() + ", 状态为" + thread.getState());
		}
	}
	
	private static class StatusThread extends Thread {
		
		public StatusThread() {
			super("statusThread" + ++threadSeqNumber);
			//加入全局静态线程列表
			addStatusThread(this);
		}
		
		@Override
		public void run() {
			Print.cfo(getName() + ", 状态为:" + getState());
			for (int i = 0; i < MAX; i++) {
				//当前线程睡眠再打印所有线程状态
				ThreadUtil.sleepMilliseconds(500);
				printTheadStatus();
			}
			Print.cfo(getName() + " 运行结束.");
		}
		
	}
	
}
