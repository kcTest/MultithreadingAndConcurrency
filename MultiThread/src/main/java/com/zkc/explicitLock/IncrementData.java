package com.zkc.explicitLock;

import com.zkc.util.Print;
import com.zkc.util.ThreadUtil;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

/**
 * 创建锁的代码（变化的部分）和使用锁的代码（不变的部分）进行分离
 * 将临界区使用锁的代码进行了抽取和封装，形成一个可以复用的独立类——IncrementData累加类
 */
public class IncrementData {
	
	public static int sum = 0;
	
	
	public static void lockAndFastIncrease03(Lock lock) {
		//1、限时抢锁，在抢锁时会进行一段时间的阻塞等待，其中的time参数代表最大的阻塞时长，unit参数为时长的单位（如秒）
/*
阻塞式“限时抢占”（在timeout时间内）锁抢占过程中会处理Thread.interrupt()中断信号，如果线程被中断，就会终止抢占并抛出InterruptedException异常
 */
		try {
			if (lock.tryLock(1, TimeUnit.SECONDS)) {
				try {
					//2、抢锁成功 执行临界区代码
					sum++;
				} finally {
					//3、释放锁
					lock.unlock();
				}
			} else {
				//4、抢锁失败 执行后备动作
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
	}
	
	public static void lockAndFastIncrease02(Lock lock) {
		//1、尝试非阻塞抢占，在没有抢到锁的情况下，当前线程会立即返回，不会被阻塞
		if (!lock.tryLock()) {
			try {
				//2、抢锁成功 执行临界区代码
				sum++;
			} finally {
				//3、释放锁
				lock.unlock();
			}
		} else {
			//4、抢锁失败 执行后备动作
		}
		
	}
	
	public static void lockAndFastIncrease(Lock lock) {
		/*
		lock()方法没有申明抛出异常，所以可以不包含到try块中；
		lock()方法并不一定能够抢占锁成功，如果没有抢占成功，当然也就不需要释放锁，而且在没有占有锁的情况下去释放锁，可能会导致运行时异常
		 */
		//1、阻塞式抢占锁 在没有抢到锁的情况下，当前线程会阻塞
		lock.lock();
		/*
		在抢占锁操作lock.lock()和try语句之间不要插入任何代码，避免抛出异常而导致释放锁操作lock.unlock()执行不到，导致锁无法被释放
		 */
		try {
			//2、执行临界区代码
			sum++;
		} finally {
			/*
			必须在try-catch结构的finally块中执行，否则，如果临界区代码抛出异常，锁就有可能永远得不到释放
			 */
			//3、释放锁
			lock.unlock();
		}
	}
	
	public static void lockInterruptiblyAndIncrease(Lock lock) {
		Print.syncTco(" 开始抢占锁");
		try {
			/*
			可中断抢占锁抢占过程中会处理Thread.interrupt()中断信号，如果线程被中断，就会终止抢占并抛出InterruptedException异常
			 */
			lock.lockInterruptibly();
		} catch (InterruptedException e) {
			Print.syncTco("抢占被中断，抢锁失败");
			return;
		}
		try {
			Print.syncTco("抢到了锁，同步执行1s");
			ThreadUtil.sleepMilliseconds(1000);
			sum++;
			if (Thread.currentThread().isInterrupted()) {
				Print.syncTco("同步执行被中断");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			lock.unlock();
		}
		
	}
	
}
