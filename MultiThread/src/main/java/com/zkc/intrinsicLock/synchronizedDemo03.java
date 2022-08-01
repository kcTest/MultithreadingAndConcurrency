package com.zkc.intrinsicLock;

import com.zkc.util.Print;

import java.util.concurrent.CountDownLatch;

/**
 * synchronized静态方法
 * 使用synchronized关键字修饰static方法时，synchronized的同步锁并不是普通Object对象的监视锁，而是类所对应的Class对象的监视锁
 */
public class synchronizedDemo03 {
	
	private static class SafeStaticMethodPlus {
		private static Integer amount = 0;
		
		/**
		 * 使用类锁作为synchronized的同步锁时会造成同一个JVM内的所有线程只能互斥地进入临界区段
		 */
		public static synchronized void selfPlus() {
			amount++;
		}
	}
}

