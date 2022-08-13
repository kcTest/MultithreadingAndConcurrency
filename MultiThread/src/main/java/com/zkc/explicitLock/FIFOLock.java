package com.zkc.explicitLock;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.LockSupport;

public class FIFOLock {
	
	private ConcurrentLinkedQueue<Thread> q = new ConcurrentLinkedQueue<>();
	private AtomicBoolean locked = new AtomicBoolean(false);
	
	public void lock() {
		q.add(Thread.currentThread());
		while (q.peek() != Thread.currentThread() || !locked.compareAndSet(false, true)) {
			LockSupport.park(this);
		}
		q.remove();
	}
	
	public void unlock() {
		locked.set(false);
		LockSupport.unpark(q.peek());
	}
	
}
