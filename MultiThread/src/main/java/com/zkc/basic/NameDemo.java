package com.zkc.basic;

import com.zkc.util.Print;
import com.zkc.util.ThreadUtil;

/**
 * （1）线程名称一般在启动线程前设置，但也允许为运行的线程设置名称。
 * （2）允许两个Thread对象有相同的名称，但是应该避免。
 * （3）如果程序没有为线程指定名称，系统会自动为线程设置名称。
 */
public class NameDemo {
	
	public static void main(String[] args) {
		RunTarget target = new RunTarget();
		//系统自动设置线程名称
		new Thread(target).start();
		//系统自动设置线程名称
		new Thread(target).start();
		//系统自动设置线程名称
		new Thread(target).start();
		//手动设置线程名称
		new Thread(target, "手动命名线程-A").start();
		//手动设置线程名称
		new Thread(target, "手动命名线程-B").start();
		//主线程先不结束
		ThreadUtil.sleepSeconds(Integer.MAX_VALUE);
	}
	
	private static final int MAX = 3;
	
	private static class RunTarget implements Runnable {
		
		@Override
		public void run() {
			for (int i = 0; i < MAX; i++) {
				ThreadUtil.sleepMilliseconds(500);
				Print.tfo("线程执行轮次:" + i);
			}
		}
	}
}
