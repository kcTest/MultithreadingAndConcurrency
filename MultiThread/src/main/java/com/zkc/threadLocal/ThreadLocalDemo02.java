package com.zkc.threadLocal;

import com.zkc.util.ThreadUtil;

/**
 * 记录执行过程中所调用的函数所需的执行时间（即执行耗时）
 * <p>
 * （1）尽量使用private static final修饰ThreadLocal实例。使用private与final修饰符主要是为了尽可能不让他人修改、变更
 * ThreadLocal变量的引用，使用static修饰符主要是为了确保ThreadLocal实例的全局唯一。
 * （2）ThreadLocal使用完成之后务必调用remove()方法。这是简单、有效地避免ThreadLocal引发内存泄漏问题的方法
 */
public class ThreadLocalDemo02 {
	
	public static void main(String[] args) {
		Runnable targetTask = () -> {
			//开始耗时记录
			SpeedLog.beginSpeedLog();
			//调用模拟业务的方法
			serviceMethod();
			//打印耗时
			SpeedLog.printCost();
			//结束耗时记录
			SpeedLog.endSpeedLog();
		};
		new Thread(targetTask).start();
		ThreadUtil.sleepSeconds(10);
	}
	
	private static void serviceMethod() {
		//模拟耗时
		ThreadUtil.sleepMilliseconds(500);
		//记录当前耗时
		SpeedLog.logPoint("point-1 service");
		
		//模拟耗时
		ThreadUtil.sleepMilliseconds(400);
		//记录当前耗时
		SpeedLog.logPoint("point-2 dao");
		
		//模拟耗时
		ThreadUtil.sleepMilliseconds(600);
		//记录当前耗时
		SpeedLog.logPoint("point-3 rpc");
	}
	
}
