package com.zkc.atomic;

import com.zkc.util.Print;
import com.zkc.util.ThreadUtil;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicMarkableReference;
import java.util.concurrent.atomic.AtomicStampedReference;

/**
 * 使用AtomicStampedReference解决ABA问题
 * <p>
 * AtomicMarkableReference适用于只要知道对象是否被修改过，而不适用于对象被反复修改的场景
 */
public class AtomicMarkableReferenceDemo {
	
	@Test
	public void test01() throws InterruptedException {
		CountDownLatch latch = new CountDownLatch(2);
		
		AtomicMarkableReference<Integer> atomicMarkableRef = new AtomicMarkableReference<>(1, false);
		
		/*
		通过两个程分别更新同一个atomicRef的值，第一个线程会更新成功，而第二个线程会更新失败
[appPool-1-MIXED-1]: before sleep 500, val=1 mark=false
[appPool-1-MIXED-2]: before sleep 1000, val=1 mark=false
[appPool-1-MIXED-1]: after sleep 500, CAS1 success=true val=10 mark=true
[appPool-1-MIXED-2]: after sleep 1000, mark=true
[appPool-1-MIXED-2]: after sleep 1000, CAS2 success=false val=10 stamp=true
		 */
		ThreadUtil.getMixedTargetThreadPool().submit(() -> {
			
			int val = atomicMarkableRef.getReference();
			boolean mark = getMark(atomicMarkableRef);
			Print.tco("before sleep 500, val=" + val + " mark=" + mark);
			
			ThreadUtil.sleepMilliseconds(500);
			
			boolean success = atomicMarkableRef.compareAndSet(val, 10, mark, !mark);
			Print.tco("after sleep 500, CAS1 success=" + success + " val=" + atomicMarkableRef.getReference() + " mark=" + getMark(atomicMarkableRef));
			
			latch.countDown();
		});
		
		ThreadUtil.getMixedTargetThreadPool().submit(() -> {
			
			Integer val = atomicMarkableRef.getReference();
			boolean mark = getMark(atomicMarkableRef);
			Print.tco("before sleep 1000, val=" + val + " mark=" + mark);
			
			ThreadUtil.sleepMilliseconds(1000);
			Print.tco("after sleep 1000, mark=" + getMark(atomicMarkableRef));
			
			boolean success = atomicMarkableRef.compareAndSet(val, 20, mark, !mark);
			Print.tco("after sleep 1000, CAS2 success=" + success + " val=" + atomicMarkableRef.getReference() + " stamp=" + getMark(atomicMarkableRef));
			latch.countDown();
		});
		latch.await();
	}
	
	private static boolean getMark(AtomicMarkableReference<Integer> atomicMarkableRef) {
		boolean[] markHolder = new boolean[]{false};
		atomicMarkableRef.get(markHolder);
		return markHolder[0];
	}
}
