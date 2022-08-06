package com.zkc.explicitLock;

import com.zkc.util.DateUtil;
import com.zkc.util.Print;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * ReentrantReadWriteLock不支持读锁的升级，主要是避免死锁，例如两个线程A和B都占了读锁并且都需要升级
 * 成写锁，A升级要求B释放读锁，B升级要求A释放读锁，二者就会由于相互等待形成死锁
 * <p>
 * 更适合读多写少的场景，可以提高并发读的效率
 */
public class ReentrantReadWriteLockDemo02 {
	
	public static void main(String[] args) {
		Runnable readTask = () -> get("key");
		Runnable writeTask = () -> put("key", "val");
		new Thread(readTask, "读线程").start();
		new Thread(writeTask, "写线程").start();
		/*
[写线程]: 13:47:30 抢占了WRITE_LOCK，开始执行write操作
[写线程]: 尝试降级写锁为读锁
[写线程]: 写锁降级为读锁成功
[读线程]: 13:47:31 抢占了READ_LOCK，开始执行read操作
[读线程]: 尝试升级读锁为写锁
		 */
		/*
[读线程]: 13:47:55 抢占了READ_LOCK，开始执行read操作
[读线程]: 尝试升级读锁为写锁
		 */
	}
	
	private static final Map<String, String> MAP = new HashMap<>();
	private static final ReentrantReadWriteLock LOCK = new ReentrantReadWriteLock();
	private static final ReentrantReadWriteLock.ReadLock READ_LOCK = LOCK.readLock();
	private static final ReentrantReadWriteLock.WriteLock WRITE_LOCK = LOCK.writeLock();
	
	private static Object get(String key) {
		READ_LOCK.lock();
		try {
			Print.tco(DateUtil.getNowTime() + " 抢占了READ_LOCK，开始执行read操作");
			Thread.sleep(1000);
			String s = MAP.get(key);
			Print.tco("尝试升级读锁为写锁");
			WRITE_LOCK.lock();
			Print.tco("读锁升级为写锁成功");
			return s;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			WRITE_LOCK.unlock();
			READ_LOCK.unlock();
		}
		return null;
	}
	
	private static Object put(String key, String val) {
		WRITE_LOCK.lock();
		try {
			Print.tco(DateUtil.getNowTime() + " 抢占了WRITE_LOCK，开始执行write操作");
			Thread.sleep(1000);
			String s = MAP.put(key, val);
			Print.tco("尝试降级写锁为读锁");
			READ_LOCK.lock();
			Print.tco("写锁降级为读锁成功");
			return s;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			READ_LOCK.unlock();
			WRITE_LOCK.unlock();
		}
		return null;
	}
}
