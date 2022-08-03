package com.zkc.intrinsicLock;

import com.zkc.util.Print;
import com.zkc.util.ThreadUtil;
import org.openjdk.jol.vm.VM;

import java.util.concurrent.CountDownLatch;

/**
 * 在多线程竞争不激烈的情况下，通过CAS机制竞争锁减少重量级锁产生的性能损耗
 * <p>
 * 锁记录是线程私有的，每个线程都有自己的一份锁记录，在创建完锁记录后，会将内置锁对象的Mark Word复制到锁记录的Displaced Mark Word字段
 * 因为内置锁对象的Mark Word的结构会有所变化，Mark Word将会出现一个指向锁记录的指针，而不再存着无锁状态下的锁对象哈希码等信息，
 * 所以必须将这些信息暂存起来，供后面在锁释放时使用
 * <p>
 * 轻量级锁主要有两种：普通自旋锁和自适应自旋锁
 */
public class ThinLockDemo {
	
	public static void main(String[] args) throws InterruptedException {
		Print.tcfo(VM.current().details());
		ThreadUtil.sleepMilliseconds(5000);
		ObjectLock lock = new ObjectLock();
		Print.tcfo("抢占锁前，lock状态:");
		/*输出与前面偏向锁演示实例的输出相同*/
		lock.printObjectStructure();
		ThreadUtil.sleepMilliseconds(5000);
		
		CountDownLatch latch = new CountDownLatch(2);
		Runnable targetTask = () -> {
			for (int i = 0; i < MAX; i++) {
				synchronized (lock) {
					lock.increase();
					if (i == 1) {
						Print.tcfo("第一个线程占有锁, lock的状态：");
						lock.printObjectStructure();
						/*
						第一个抢锁线程，在抢占完成之后，ObjectLock实例的锁状态还是为偏向锁
						 */
					}
				}
			}
			latch.countDown();
			/*
JVM检查原来持有该对象锁的占有线程是否依然存活，如果挂了，就可以将对象变为无锁状态，然后进行重新偏向，偏向抢锁线程
如果JVM检查到原来的线程依然存活，就进一步检查占有线程的调用堆栈是否通过锁记录持有偏向锁。如果存在锁记录，就表明原来的
线程还在使用偏向锁，发生锁竞争，撤销原来的偏向锁，将偏向锁膨胀（INFLATING）为轻量级锁。
			 */
			// 锁已经释放 死循环导致线程一直存在
			
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
					if (i == 1) {
						Print.tcfo("第二个线程占有锁, lock的状态：");

//有多个线程来竞争偏向锁，此对象锁已经有所偏向，其他的线程发现偏向锁并不是偏向自己，就说明存在了竞争，尝试撤销偏向锁（很可能引入安全点），然后膨胀到轻量级锁\
// 第二个线程抢锁成功之后，ObjectLock实例的锁状态为轻量级锁
						/*
[thinLock-demo-thread|ThinLockDemo:lambda$main$1]: 第二个线程占有锁, lock的状态：
[ObjectLock:printObjectStructure]: lock = com.zkc.intrinsicLock.ObjectLock object internals:
OFF  SZ   TYPE DESCRIPTION               VALUE
  0   8        (object header: mark)     0x00000019537ff400 (thin lock: 0x00000019537ff400)
  8   4        (object header: class)    0xf801241f
 12   4    int ObjectLock.amount         1501
Instance size: 16 bytes
Space losses: 0 bytes internal + 0 bytes external = 0 bytes total						 
						 */
						lock.printObjectStructure();
					}
				}
			}
			latch.countDown();
		};
		new Thread(targetTask2, "thinLock-demo-thread").start();
		
		//等待加锁线程执行完成
		latch.await();
		ThreadUtil.sleepMilliseconds(2000);
		Print.tcfo("释放锁后，lock的状态：");
		lock.printObjectStructure();
		//轻量级锁被释放之后，ObjectLock实例变成无锁状态，其lock标记位改为01（无锁标志）
		/*
[main|ThinLockDemo:main]: 释放锁后，lock的状态：
[ObjectLock:printObjectStructure]: lock = com.zkc.intrinsicLock.ObjectLock object internals:
OFF  SZ   TYPE DESCRIPTION               VALUE
  0   8        (object header: mark)     0x0000000000000001 (non-biasable; age: 0)
  8   4        (object header: class)    0xf801241f
 12   4    int ObjectLock.amount         2000
Instance size: 16 bytes
Space losses: 0 bytes internal + 0 bytes external = 0 bytes total
		 */
		
	}
	
	private static final int MAX = 1000;
}
