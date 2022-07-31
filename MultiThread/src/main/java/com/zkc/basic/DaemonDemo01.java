package com.zkc.basic;

import com.zkc.util.Print;
import com.zkc.util.ThreadUtil;

/**
 * 只要JVM实例中尚存在任何一个用户线程没有结束，守护线程就能执行自己的工作；只有当最后一个用户线程结束，守护线程随着JVM一同结束工作,
 * 守护线程全部被终止，JVM虚拟机不一定终止
 * <p>
 * （1）守护线程必须在启动前将其守护状态设置为true，启动之后不能再将用户线程设置为守护线程，否则JVM会抛出一个InterruptedException异常。
 * <p>   具体来说，如果线程为守护线程，就必须在线程实例的start()方法调用之前调用线程实例的setDaemon(true)，设置其daemon实例属性值为true。
 * （2）守护线程存在被JVM强行终止的风险，所以在守护线程中尽量不去访问系统资源，如文件句柄、数据库连接等。
 * <p>   守护线程被强行终止时，可能会引发系统资源操作不负责任的中断，从而导致资源不可逆的损坏。
 * （3）守护线程创建的线程也是守护线程。在守护线程中创建的线程，新的线程都是守护线程。
 * <p>   在创建之后，如果通过调用setDaemon(false)将新的线程显式地设置为用户线程，新的线程可以调整成用户线程。
 */
public class DaemonDemo01 {
	
	public static void main(String[] args) {
		Thread daemonThread = new DaemonThread();
		daemonThread.setDaemon(true);
		daemonThread.start();
		
		//创建用户线程 执行4轮
		Thread userThread = new Thread(() -> {
			Print.syncTco(">>用户线程开始.");
			for (int i = 0; i < MAX; i++) {
				Print.syncTco(">>轮次：" + i);
				Print.syncTco(">>守护状态为:" + ThreadUtil.getCurrentThread().isDaemon());
				ThreadUtil.sleepMilliseconds(SLEEP_GAP);
			}
			Print.syncTco(">>用户线程结束.");
		}, "userThread");
		userThread.start();
		Print.syncTco(" 守护状态为:" + ThreadUtil.getCurrentThread().isDaemon());
		Print.syncTco(" 运行结束");
	}
	
	private static final int SLEEP_GAP = 500;
	private static final int MAX = 4;
	
	private static class DaemonThread extends Thread {
		
		public DaemonThread() {
			super("daemonThread");
		}
		
		@Override
		public void run() {
			Print.syncTco("--daemon线程开始");
			for (int i = 1; ; i++) {
				Print.syncTco("--轮次：" + i);
				Print.syncTco("--守护状态为:" + isDaemon());
				ThreadUtil.sleepMilliseconds(SLEEP_GAP);
			}
		}
	}
}
