package com.zkc.explicitLock;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class ReentrantLockDemo04 {
	
	public static void main(String[] args) {
		ReentrantLockDemo04 reentrantLockDemo02 = new ReentrantLockDemo04();
		new Thread(() -> reentrantLockDemo02.take()).start();
		new Thread(() -> reentrantLockDemo02.put()).start();
	}
	
	/**
	 * 创建一个显式锁
	 */
	private static final ReentrantLock LOCK = new ReentrantLock();
	/**
	 * 不能独立创建一个Condition对象，而是需要借助于显式锁实例去获取其绑定的Condition对象
	 */
	private static final Condition CONDITION = LOCK.newCondition();
	
	public Integer take() {
		try {
			LOCK.lockInterruptibly();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		try {
			CONDITION.await();
			System.out.println("ending...");
			return 0;
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		} finally {
			LOCK.unlock();
		}
	}
	
	public void put() {
		try {
			LOCK.lockInterruptibly();
			CONDITION.signal();
			Thread.sleep(1000000);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			LOCK.unlock();
		}
	}
}
