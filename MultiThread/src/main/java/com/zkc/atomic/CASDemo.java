package com.zkc.atomic;

import com.zkc.util.JvmUtil;
import com.zkc.util.Print;
import com.zkc.util.ThreadUtil;
import sun.misc.Unsafe;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * （1）获得字段的期望值（oldValue）。
 * （2）计算出需要替换的新值（newValue）。
 * （3）通过CAS将新值（newValue）放在字段的内存地址上，如果CAS失败就重复第（1）步到第（2）步，一直到CAS成功，这种重复俗称CAS自旋
 */
public class CASDemo {
	
	public static void main(String[] args) throws InterruptedException {
		OptimisticLockPlus plus = new OptimisticLockPlus();
		CountDownLatch latch = new CountDownLatch(THREAD_COUNT);
		for (int i = 0; i < THREAD_COUNT; i++) {
			//提交10个任务
			ThreadUtil.getMixedTargetThreadPool().submit(() -> {
				//每个任务里增加1000次
				for (int j = 0; j < 1000; j++) {
					plus.selfPlus();
				}
				latch.countDown();
			});
		}
		latch.await();
		Print.tco("累加和：" + plus.val);
		Print.tco("失败次数：" + OptimisticLockPlus.failNum);
	}
	
	/**
	 * 并发数量
	 */
	private static final int THREAD_COUNT = 10;
	
	/**
	 * 基于CAS无锁实现的安全自增
	 */
	private static class OptimisticLockPlus {
		
		/**
		 * volatile关键字可以保证任何线程在任何时刻总能拿到该变量的最新值，其目的在于保障变量值的线程可见性
		 */
		private volatile int val;
		/**
		 * 不安全类
		 */
		private static final Unsafe unsafe = JvmUtil.getUnsafe();
		/**
		 * val字段相对于对象头部地址的偏移量
		 */
		private static final long valueOffset;
		
		/**
		 * 统计失败的次数
		 */
		public static final AtomicInteger failNum = new AtomicInteger(0);
		
		static {
			try {
				//取得val字段的内存相对偏移量
				/*
Mark Word占用了8字节（64位），压缩过的Class Pointer占用了4字节。
接在Object Header之后的就是成员属性val的内存区域，所以value属性相对于Object Header的偏移量为12
				 */
				valueOffset = unsafe.objectFieldOffset(OptimisticLockPlus.class.getDeclaredField("val"));
				Print.tco("val offset=" + valueOffset);
			} catch (Exception e) {
				throw new Error();
			}
		}
		
		/**
		 * 通过CAS原子操作 进行 比较并交换
		 */
		public final boolean unSafeCompareAndSet(int oldValue, int newValue) {
			return unsafe.compareAndSwapInt(this, valueOffset, oldValue, newValue);
		}
		
		/**
		 * 无锁实现安全的自增
		 */
		public void selfPlus() {
			int oldVal;
			int i = 0;
			//如果操作失败就自旋 一直到操作成功
			do {
				//获取最新值
				oldVal = val;
				//统计自旋失败的次数
				if (i++ > 1) {
					//记录失败的次数
					failNum.incrementAndGet();
				}
			} while (!unSafeCompareAndSet(oldVal, oldVal + 1));
		}
		
	}
}
