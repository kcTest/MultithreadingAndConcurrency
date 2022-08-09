package com.zkc.util;

import java.util.concurrent.Callable;

public class ShutdownHookThread extends Thread {
	private volatile boolean hasShutdown = false;
	private final Callable<Void> callBack;
	
	public ShutdownHookThread(String name, Callable<Void> callBack) {
		super("JVM退出钩子(" + name + ")  ");
		this.callBack = callBack;
	}
	
	@Override
	public void run() {
		synchronized (this) {
			System.out.println(getName() + "starting.....");
			if (!this.hasShutdown) {
				this.hasShutdown = true;
				long beginTime = System.currentTimeMillis();
				try {
					this.callBack.call();
				} catch (Exception e) {
					System.out.println(getName() + "error" + e.getMessage());
				}
				System.out.println(getName() + "耗时(ms)" + (System.currentTimeMillis() - beginTime));
			}
		}
	}
	
}
