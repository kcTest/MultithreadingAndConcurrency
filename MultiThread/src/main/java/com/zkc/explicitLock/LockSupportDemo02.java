package com.zkc.explicitLock;

import java.util.concurrent.locks.LockSupport;

public class LockSupportDemo02 {
	
	public static void main(String[] args) throws InterruptedException {
		FIFOLock fifoLock = new FIFOLock();
		fifoLock.lock();
		Thread thread = new Thread(() -> {
			System.out.println(Thread.currentThread().getName() + " about to park itself.");
			fifoLock.lock();
		});
		thread.start();
		Thread.sleep(2000);
		Object blocker = LockSupport.getBlocker(thread);
		System.out.println(blocker);
		fifoLock.unlock();
	}
	
	
}
