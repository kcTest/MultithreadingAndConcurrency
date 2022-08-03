package com.zkc.intrinsicLock;

import com.zkc.util.Print;
import com.zkc.util.ThreadUtil;
import org.openjdk.jol.vm.VM;

import java.util.concurrent.CountDownLatch;

/**
 * 主要作用是消除无竞争情况下的同步原语假如在大部分情况下同步块是没有竞争的，那么可以通过偏向来提高性能
 * 如果锁对象时常被多个线程竞争，偏向锁就是多余的，并且其撤销的过程会带来一些性能开销
 */
public class BiasedLockDemo {
	
	public static void main(String[] args) throws InterruptedException {
		Print.tcfo(VM.current().details());
		/*
		JVM在启动的时候会延迟启用偏向锁机制。JVM默认把偏向锁延迟了4000毫秒  程序要等待5秒
		因为JVM在启动的时候需要加载资源，这些对象加上偏向锁没有任何意义，不启用偏向锁能减少大量偏向锁撤销的成本
		
		如果不想等待（在代码中让线程睡眠），可以直接通过修改JVM的启动选项来禁止偏向锁延迟，
		其具体的启动选项如下：-XX:+UseBiasedLocking -XX:BiasedLockingStartupDelay=0
        具体使用的方式为：java -XX:+UseBiasedLocking -XX:BiasedLockingStartupDelay=0 mainclass
		 */
		ThreadUtil.sleepMilliseconds(5000);
		ObjectLock lock = new ObjectLock();
		Print.tcfo("抢占锁前，lock状态:");
		/*
[main|BiasedLockDemo:main]: # Running 64-bit HotSpot VM.
# Using compressed oop with 3-bit shift.  //对oop（普通对象）、klass（类对象）指针都进行了压缩 
# Using compressed klass with 3-bit shift.  //类对象的名称使用了klass而不是class，主要是为了避开使用class（作为关键字在定义类时使用）导致的误解
# Objects are 8 bytes aligned.
# Field sizes by type: 4, 1, 1, 2, 2, 4, 4, 8, 8 [bytes]
# Array element sizes: 4, 1, 1, 2, 2, 4, 4, 8, 8 [bytes]

[main|BiasedLockDemo:main]: 抢占锁前，lock状态:
[ObjectLock:printObjectStructure]: lock = com.zkc.intrinsicLock.ObjectLock object internals:
OFF  SZ   TYPE DESCRIPTION               VALUE
  0   8        (object header: mark)     0x0000000000000005 (biasable; age: 0)
  8   4        (object header: class)    0xf801a9e1
 12   4    int ObjectLock.amount         0
Instance size: 16 bytes
Space losses: 0 bytes internal + 0 bytes external = 0 bytes total
		 */
//	biased_lock（偏向锁）状态已经启用	biased_lock和lock组合在一起为101，表明当前的ObjectLock实例处于偏向锁状态
		lock.printObjectStructure();
		ThreadUtil.sleepMilliseconds(5000);
		
		CountDownLatch latch = new CountDownLatch(1);
		Runnable targetTask = () -> {
			for (int i = 0; i < MAX; i++) {
				synchronized (lock) {
					lock.increase();
					if (i == (MAX >> 1)) {
						Print.tcfo("占有锁, lock的状态：");
						lock.printObjectStructure();
						/*
						已经记录了其偏向的线程ID，不过由于此线程ID不是Java中的Thread实例的ID
						*/
						/*
[ObjectLock:printObjectStructure]: lock = com.zkc.intrinsicLock.ObjectLock object internals:
OFF  SZ   TYPE DESCRIPTION               VALUE
  0   8        (object header: mark)     0x00000161722af005 (biased: 0x00000000585c8abc; epoch: 0; age: 0)
  8   4        (object header: class)    0xf801241f
 12   4    int ObjectLock.amount         501
Instance size: 16 bytes
Space losses: 0 bytes internal + 0 bytes external = 0 bytes total
						 */
					}
				}
				//每次等待10ms
				ThreadUtil.sleepMilliseconds(10);
			}
			latch.countDown();
		};
		new Thread(targetTask, "biased-demo-thread").start();
		//等待加锁线程执行完成
		latch.await();
		Print.tcfo("释放锁后，lock的状态：");
		lock.printObjectStructure();
		/*
		抢锁的线程已经结束，但是ObjectLock实例的对象结构仍然记录了其之前的偏向线程ID，其锁状态还是偏向锁状态101
		*/
		/*
[main|BiasedLockDemo:main]: 释放锁后，lock的状态：
[ObjectLock:printObjectStructure]: lock = com.zkc.intrinsicLock.ObjectLock object internals:
OFF  SZ   TYPE DESCRIPTION               VALUE
  0   8        (object header: mark)     0x00000161722af005 (biased: 0x00000000585c8abc; epoch: 0; age: 0)
  8   4        (object header: class)    0xf801241f
 12   4    int ObjectLock.amount         1000
Instance size: 16 bytes
Space losses: 0 bytes internal + 0 bytes external = 0 bytes total
		 */
		
	}
	
	private static final int MAX = 1000;
}
