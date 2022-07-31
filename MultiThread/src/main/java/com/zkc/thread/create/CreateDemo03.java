package com.zkc.thread.create;

import com.zkc.util.Print;
import com.zkc.util.ThreadUtil;

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

/**
 * FutureTask类和Callable接口的联合使用可以创建能够获取异步执行结果的线程
 * <p>
 * （1）创建一个Callable接口的实现类，并实现其call()方法，编写好异步执行的具体逻辑，可以有返回值。
 * （2）使用Callable实现类的实例构造一个FutureTask实例。
 * （3）使用FutureTask实例作为Thread构造器的target入参，构造新的Thread线程实例。
 * （4）调用Thread实例的start()方法启动新线程，启动新线程的run()方法并发执行。其内部的执行过程为：启动Thread实例的run()
 * <p>  方法并发执行后，会执行FutureTask实例的run()方法，最终会并发执行Callable实现类的call()方法。
 * （5）调用FutureTask对象的get()方法阻塞性地获得并发线程的执行结果。
 */
public class CreateDemo03 {
	
	public static void main(String[] args) throws InterruptedException {
		ReturnableTask returnableTask = new ReturnableTask();
		FutureTask<Long> futureTask = new FutureTask<>(returnableTask);
		Thread thread = new Thread(futureTask, "returnableThread");
		thread.start();
		Thread.sleep(500);
		Print.cfo(ThreadUtil.getCurThreadName() + " 让子弹飞一会儿");
		Print.cfo(ThreadUtil.getCurThreadName() + " 做一点自己的事情.");
		for (int i = 0; i < (COMPUTE_TIMES / 2); i++) {
			int j = i * 10000;
		}
		Print.cfo(ThreadUtil.getCurThreadName() + " 获取并发任务的执行结果");
		try {
			//FutureTask的Callable成员的call()方法执行完成后，会将结果保存在FutureTask内部的outcome实例属性中
			Print.cfo(thread.getName() + " 线程占用时间：" + futureTask.get());
		} catch (Exception e) {
			e.printStackTrace();
		}
		Print.cfo(ThreadUtil.getCurThreadName() + " 运行结束.");
	}
	
	private static final int COMPUTE_TIMES = 10000000;
	
	private static class ReturnableTask implements Callable<Long> {
		
		@Override
		public Long call() throws Exception {
			long startTime = System.currentTimeMillis();
			Print.cfo(ThreadUtil.getCurThreadName() + " 线程运行开始.");
			Thread.sleep(1000);
			for (int i = 0; i < COMPUTE_TIMES; i++) {
				int j = i * 10000;
			}
			long used = System.currentTimeMillis() - startTime;
			Print.cfo(ThreadUtil.getCurThreadName() + " 线程运行结束。");
			return used;
		}
	}
	
}
