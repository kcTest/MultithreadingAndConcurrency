package com.zkc.intrinsicLock;

import com.zkc.util.Print;
import com.zkc.util.ThreadUtil;
import org.openjdk.jol.vm.VM;

import java.util.concurrent.CountDownLatch;

/**
 * 在争用激烈的场景下，轻量级锁会膨胀为基于操作系统内核互斥锁实现的重量级锁
 */
public class FatLockDemo {
	
	public static void main(String[] args) throws InterruptedException {
		Print.tcfo(VM.current().details());
		ThreadUtil.sleepMilliseconds(5000);
		ObjectLock lock = new ObjectLock();
		Print.tcfo("抢占锁前，lock状态:");
		/*程序启动运行5秒之后，ObjectLock的锁状态为偏向锁*/
		lock.printObjectStructure();
		ThreadUtil.sleepMilliseconds(1000);
		
		CountDownLatch latch = new CountDownLatch(3);
		Runnable targetTask = () -> {
			for (int i = 0; i < MAX; i++) {
				synchronized (lock) {
					lock.increase();
					if (i == 0) {
						Print.tcfo("第一个线程占有锁, lock的状态：");
						lock.printObjectStructure();
						/*有一个线程占有锁，此时的ObjectLock实例的锁状态仍然为偏向锁*/
					}
				}
			}
			latch.countDown();
			for (int i = 0; ; i++) {
				ThreadUtil.sleepMilliseconds(1);
			}
		};
		new Thread(targetTask, "biased-demo-thread").start();
		
		ThreadUtil.sleepMilliseconds(1000);
		
		Runnable targetTask2 = () -> {
			for (int i = 0; i < MAX; i++) {
				synchronized (lock) {
					lock.increase();
					if (i == 0) {
						Print.tcfo("当前线程占有锁, lock的状态：");
						lock.printObjectStructure();
						/*thread1 此时ObjectLock实例的锁状态已经膨胀为轻量级锁，其lock标记为00*/
						/*thread2 此时ObjectLock实例的锁状态已经从轻量级锁膨胀为重量级锁，其lock标记为10*/
						/*
[thinLock-demo-thread2|HeavyLockDemo:lambda$main$1]: 当前线程占有锁, lock的状态：
[ObjectLock:printObjectStructure]: lock = com.zkc.intrinsicLock.ObjectLock object internals:
OFF  SZ   TYPE DESCRIPTION               VALUE
  0   8        (object header: mark)     0x0000026bf778a19a (fat lock: 0x0000026bf778a19a)
  8   4        (object header: class)    0xf801241f
 12   4    int ObjectLock.amount         1010
Instance size: 16 bytes
Space losses: 0 bytes internal + 0 bytes external = 0 bytes total
						 */
					}
					//synchronized内 每次等待1ms 第三个线程执行时第二个线程还未执行完成并且占用锁 导致升级为重量级锁
					ThreadUtil.sleepMilliseconds(1);
				}
			}
			latch.countDown();
		};
		new Thread(targetTask2, "thinLock-demo-thread1").start();
		ThreadUtil.sleepMilliseconds(100);
		new Thread(targetTask2, "fatLock-demo-thread2").start();
		
		//等待加锁线程执行完成
		latch.await();
		ThreadUtil.sleepMilliseconds(2000);
		Print.tcfo("释放锁后，lock的状态：");
		lock.printObjectStructure();
		//重量级锁被释放之后，ObjectLock实例变成无锁状态，其lock标记位改为01（无锁标志）
	}
	
	private static final int MAX = 1000;
}
