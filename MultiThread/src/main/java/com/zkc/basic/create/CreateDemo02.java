package com.zkc.basic.create;

import com.zkc.util.Print;
import com.zkc.util.ThreadUtil;

/**
 * 通过实现Runnable接口创建线程类
 * （1）定义一个新类实现Runnable接口。
 * （2）实现Runnable接口中的run()抽象方法，将线程代码逻辑存放在该run()实现版本中。
 * （3）通过Thread类创建线程对象，将Runnable实例作为实际参数传递给Thread类的构造器，Thread构造器将该Runnable实例赋值给自己的target执行目标属性。
 * （4）调用Thread实例的start()方法启动线程。
 * （5）线程启动之后，线程的run()方法将被JVM执行，该run()方法将调用target属性的run()方法，
 * <p>
 * <p>
 * 通过实现Runnable接口的方式创建线程目标类有以下缺点：
 * （1）所创建的类并不是线程类，而是线程的target执行目标类，需要将其实例作为参数传入线程类的构造器，才能创建真正的线程。
 * （2）如果访问当前线程的属性（甚至控制当前线程），不能直接访问Thread的实例方法，必须通过Thread.currentThread()获取当前线程实例，才能访问和控制当前线程。
 * <p>
 * 通过实现Runnable接口的方式创建线程目标类有以下优点：
 * （1）可以避免由于Java单继承带来的局限性。如果异步逻辑所在类已经继承了一个基类，就没有办法再继承Thread类。
 * <p> 比如，当一个Dog类继承了Pet类，再要继承Thread类就不行了。所以在已经存在继承关系的情况下，只能使用实现Runnable接口的方式。
 * （2）逻辑和数据更好分离。通过实现Runnable接口的方法创建多线程更加适合同一个资源被多段业务逻辑并行处理的场景。
 * <p> 在同一个资源被多个线程逻辑异步、并行处理的场景中，通过实现Runnable接口的方式设计多个target执行目标类可以更加方便、清晰地将执行逻
 * <p> 辑和数据存储分离，更好地体现了面向对象的设计思想。
 */
public class CreateDemo02 {
	
	public static void main(String[] args) {
		for (int i = 0; i < 2; i++) {
			RunTarget runTarget = new RunTarget();
			Thread thread = new Thread(runTarget, "RunnableThread" + threadNo++);
			thread.start();
		}
		Print.cfo(Thread.currentThread().getName() + " 运行结束.");
	}
	
	private static final int MAX = 5;
	private static int threadNo = 1;
	
	private static class RunTarget implements Runnable {
		@Override
		public void run() {
			for (int i = 0; i < MAX; i++) {
				Print.cfo(ThreadUtil.getCurThreadName() + ",轮次：" + i);
			}
			Print.cfo(ThreadUtil.getCurThreadName() + " 运行结束.");
		}
	}
	
}
