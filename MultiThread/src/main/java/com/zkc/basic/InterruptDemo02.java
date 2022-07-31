package com.zkc.basic;

import com.zkc.util.Print;
import com.zkc.util.ThreadUtil;
import org.junit.Test;

/**
 * Thread.interrupt()方法 只是改变中断状态，不会中断一个正在运行的线程，线程是否停止执
 * 行，需要用户程序去监视线程的isInterrupted()状态，并进行相应的处理。
 */
public class InterruptDemo02 {
	
	@Test
	public void testInterrupted() {
		Thread thread = new Thread() {
			@Override
			public void run() {
				Print.cfo("线程启动了.");
				while (true) {
					Print.cfo("isInterrupted:" + isInterrupted());
					ThreadUtil.sleepMilliseconds(5000);
					if (isInterrupted()) {
						Print.cfo("线程结束了");
						return;
					}
				}
			}
			
		};
		thread.start();
		ThreadUtil.sleepSeconds(2);
		thread.interrupt();
		ThreadUtil.sleepSeconds(2);
		thread.interrupt();
	}
	
	
}
