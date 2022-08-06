package com.zkc.explicitLock;

import com.zkc.util.DateUtil;
import com.zkc.util.Print;
import com.zkc.util.ThreadUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.StampedLock;

/**
 * StampedLock的三种模式如下：
 * （1）悲观读锁：与ReadWriteLock的读锁类似，多个线程可以同时获取悲观读锁，悲观读锁是一个共享锁。
 * （2）乐观读锁：相当于直接操作数据，不加任何锁，连读锁都不要。
 * （3）写锁：与ReadWriteLock的写锁类似，写锁和悲观读锁是互斥的。虽然写锁与乐观读锁不会互斥，但是在数据被更新之后，之前通过乐观读锁获得的数据已经变成了脏数据
 */
public class StampedLockDemo {
	
	public static void main(String[] args) {
		Runnable readTask = () -> optimisticRead("key");
		Runnable writeTask = () -> put("key", "val");
		new Thread(readTask, "读线程").start();
		new Thread(writeTask, "写线程").start();
		/*
[写线程]: 14:19:42 抢占了WRITE_LOCK，开始执行write操作
[读线程]: 14:19:42 乐观读的印戳值获取成功
[写线程]: 释放了WRITE_LOCK
[读线程]: 14:19:43 乐观读的印戳值已经过期
[读线程]: LOCK 进入过写模式，只能悲观读
[读线程]: 14:19:43 抢占了READ_LOCK，开始执行read操作
[读线程]: 释放了READ_LOCK
		 */
	}
	
	private static final Map<String, String> MAP = new HashMap<>();
	private static final StampedLock STAMPED_LOCK = new StampedLock();
	
	/**
	 * 对共享数据乐观读操作
	 */
	private static Object optimisticRead(String key) {
		String s;
		//尝试进行乐观读
		long stamp = STAMPED_LOCK.tryOptimisticRead();
		//表示当前为写模式
		if (stamp == 0) {
			Print.tco(DateUtil.getNowTime() + " 乐观读的印戳值获取失败");
			//LOCK已经进入了写模式 使用悲观读方法
			return pessimisticRead(key);
		} else {
			Print.tco(DateUtil.getNowTime() + " 乐观读的印戳值获取成功");
			//模拟耗时1s
			ThreadUtil.sleepSeconds(1);
			s = MAP.get(key);
			//乐观读操作已经间隔了一段时间 期间可能发生写入 所以需要验证乐观读的印戳值是否仍然有效 即判断LOCK是否进入过写模式
			if (!STAMPED_LOCK.validate(stamp)) {
				//乐观读的印戳值已经无效 表明写锁被占用过
				Print.tco(DateUtil.getNowTime() + " 乐观读的印戳值已经过期");
				//写锁已经被抢占 进入了写模式 只能通过悲观读再一次读取最新值
				return pessimisticRead(key);
			} else {
				//乐观读的印戳值还有效 表明写锁没有被占用过 不用悲观读锁而是直接读 减少了读锁的开销
				Print.tco(DateUtil.getNowTime() + " 乐观读的印戳值没有过期");
				return s;
			}
		}
	}
	
	
	/**
	 * 对共享数据悲观读操作
	 */
	private static Object pessimisticRead(String key) {
		Print.tco("LOCK 进入过写模式，只能悲观读");
		//尝试获取读锁印戳
		long stamp = STAMPED_LOCK.readLock();
		try {
			//成功获取到读锁 并重新获取最新的变量值
			Print.tco(DateUtil.getNowTime() + " 抢占了READ_LOCK，开始执行read操作");
			Thread.sleep(1000);
			return MAP.get(key);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			Print.tco("释放了READ_LOCK");
			STAMPED_LOCK.unlockRead(stamp);
		}
		return null;
	}
	
	/**
	 * 对共享数据写操作
	 */
	private static Object put(String key, String val) {
		//尝试获取写锁的印戳
		long stamp = STAMPED_LOCK.writeLock();
		try {
			Print.tco(DateUtil.getNowTime() + " 抢占了WRITE_LOCK，开始执行write操作");
			Thread.sleep(1000);
			return MAP.put(key, val);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			Print.tco("释放了WRITE_LOCK");
			//释放写锁
			STAMPED_LOCK.unlockWrite(stamp);
		}
		return null;
	}
}
