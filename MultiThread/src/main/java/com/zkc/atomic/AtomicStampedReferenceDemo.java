package com.zkc.atomic;

import com.zkc.util.Print;
import com.zkc.util.ThreadUtil;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicStampedReference;

/**
 * 使用AtomicStampedReference解决ABA问题
 * <p>
 * AtomicStampReference的compareAndSet()方法首先检查当前的对象引用值是否等于预期引用，并且当前印戳（Stamp）标志是否等于预
 * 期标志，如果全部相等，就以原子方式将引用值和印戳（Stamp）标志的值更新为给定的更新值
 */
public class AtomicStampedReferenceDemo {
	
	@Test
	public void test01() throws InterruptedException {
		CountDownLatch latch = new CountDownLatch(2);
		
		AtomicStampedReference<Integer> atomicStampedRef = new AtomicStampedReference<>(1, 0);
		
		/*
		过两个线程分别带上印戳更新同一个atomicStampedRef实例的值，第一个线程会更新成功，而第二个线程会更新失败
[appPool-1-MIXED-1]: before sleep 500, val=1 stamp=0
[appPool-1-MIXED-2]: before sleep 1000, val=1 stamp=0
[appPool-1-MIXED-1]: after sleep 500, CAS1 success=true val=10 stamp=1
[appPool-1-MIXED-1]: after sleep 500, CAS2 success=true val=1 stamp=2
[appPool-1-MIXED-2]: after sleep 1000, stamp=2
[appPool-1-MIXED-2]: after sleep 1000, CAS3 success=false val=1 stamp=2
		 */
		ThreadUtil.getMixedTargetThreadPool().submit(() -> {
			
			int val = atomicStampedRef.getReference();
			int stamp = atomicStampedRef.getStamp();
			Print.tco("before sleep 500, val=" + val + " stamp=" + stamp);
			
			ThreadUtil.sleepMilliseconds(500);
			
			boolean success = atomicStampedRef.compareAndSet(val, 10, stamp, ++stamp);
			Print.tco("after sleep 500, CAS1 success=" + success + " val=" + atomicStampedRef.getReference() + " stamp=" + atomicStampedRef.getStamp());
			
			success = atomicStampedRef.compareAndSet(10, 1, stamp, ++stamp);
			Print.tco("after sleep 500, CAS2 success=" + success + " val=" + atomicStampedRef.getReference() + " stamp=" + atomicStampedRef.getStamp());
			
			latch.countDown();
		});
		
		ThreadUtil.getMixedTargetThreadPool().submit(() -> {
			
			Integer val = atomicStampedRef.getReference();
			int stamp = atomicStampedRef.getStamp();
			Print.tco("before sleep 1000, val=" + val + " stamp=" + stamp);
			
			ThreadUtil.sleepMilliseconds(1000);
			Print.tco("after sleep 1000, stamp=" + atomicStampedRef.getStamp());
			
			boolean success = atomicStampedRef.compareAndSet(val, 20, stamp, ++stamp);
			Print.tco("after sleep 1000, CAS3 success=" + success + " val=" + atomicStampedRef.getReference() + " stamp=" + atomicStampedRef.getStamp());
			latch.countDown();
		});
		latch.await();
	}
}
