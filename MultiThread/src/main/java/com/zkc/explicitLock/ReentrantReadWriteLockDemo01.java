package com.zkc.explicitLock;

import com.sun.deploy.pings.Pings;
import com.zkc.util.DateUtil;
import com.zkc.util.Print;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 读写锁的内部包含两把锁：一把是读（操作）锁，是一种共享锁；另一把是写（操作）锁，是一种独占锁
 * <p>
 * ReentrantReadWriteLock类能获取读锁和写锁，它的读锁是可以多线程共享的共享锁，而它的写锁是排他锁，在被占时不允许其
 * 他线程再抢占操作。然而其读锁和写锁之间是有关系的：同一时刻不允许读锁和写锁同时被抢占，二者之间是互斥的
 */
public class ReentrantReadWriteLockDemo01 {
	
	public static void main(String[] args) {
		Runnable readTask = () -> get("key");
		Runnable writeTask = () -> put("key", "val");
		for (int i = 1; i <= 4; i++) {
			Thread thread = new Thread(readTask, "读线程" + i);
			thread.start();
		}
		for (int i = 1; i <= 2; i++) {
			Thread thread = new Thread(writeTask, "写线程" + i);
			thread.start();
		}
		/*
[读线程2]: 13:38:31 抢占了READ_LOCK，开始执行read操作
[读线程1]: 13:38:31 抢占了READ_LOCK，开始执行read操作
[读线程4]: 13:38:31 抢占了READ_LOCK，开始执行read操作
[写线程2]: 13:38:32 抢占了WRITE_LOCK，开始执行write操作  
[写线程1]: 13:38:33 抢占了WRITE_LOCK，开始执行write操作
[读线程3]: 13:38:34 抢占了READ_LOCK，开始执行read操作 //等写锁释放才能抢到读锁
		 */
	}
	
	/**
	 * 共享数据
	 */
	private static final Map<String, String> MAP = new HashMap<>();
	private static final ReentrantReadWriteLock LOCK = new ReentrantReadWriteLock();
	private static final ReentrantReadWriteLock.ReadLock READ_LOCK = LOCK.readLock();
	private static final ReentrantReadWriteLock.WriteLock WRITE_LOCK = LOCK.writeLock();
	
	private static Object get(String key) {
		//抢占读锁
		READ_LOCK.lock();
		try {
			Print.tco(DateUtil.getNowTime() + " 抢占了READ_LOCK，开始执行read操作");
			Thread.sleep(1000);
			return MAP.get(key);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			READ_LOCK.unlock();
		}
		return null;
	}
	
	private static Object put(String key, String val) {
		//抢占读锁
		WRITE_LOCK.lock();
		try {
			Print.tco(DateUtil.getNowTime() + " 抢占了WRITE_LOCK，开始执行write操作");
			Thread.sleep(1000);
			return MAP.put(key, val);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			WRITE_LOCK.unlock();
		}
		return null;
	}
}
