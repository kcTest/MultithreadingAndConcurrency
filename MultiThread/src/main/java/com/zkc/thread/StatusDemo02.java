package com.zkc.thread;

public class StatusDemo02 {
	
	public static void main(String[] args) {
		Object object = new Object();
		Thread t1 = new Thread(new Runnable() {
			@Override
			public void run() {
				synchronized (object) {
					while (true) {
					}
				}
			}
		});
		
		Thread t2 = new Thread(new Runnable() {
			@Override
			public void run() {
				synchronized (object) {
					while (true) {
					}
				}
			}
		});
		
		System.out.println(t1.getName() + " 状态：" + t1.getState());
		System.out.println(t2.getName() + " 状态：" + t2.getState());
		
		t1.start();
		System.out.println(t1.getName() + " 状态：" + t1.getState());
		
		for (int i = 0; i < 3000000; i++) {
			//适当延迟后 启动第二个线程
		}
		
		t2.start();
		System.out.println(t2.getName() + " 状态：" + t2.getState());
		
		for (int i = 0; i < 3000000; i++) {
			//适当延迟后 重新获取线程状态
		}
		
		System.out.println(t1.getName() + " 状态：" + t1.getState());
		System.out.println(t2.getName() + " 状态：" + t2.getState());
		System.out.println(Thread.currentThread().getName() + " 状态：" + Thread.currentThread().getState());
		/*
Thread-0 状态：NEW
Thread-1 状态：NEW
Thread-0 状态：RUNNABLE
Thread-1 状态：RUNNABLE
Thread-0 状态：RUNNABLE
Thread-1 状态：BLOCKED
main 状态：RUNNABLE 
		 */
	}
}
