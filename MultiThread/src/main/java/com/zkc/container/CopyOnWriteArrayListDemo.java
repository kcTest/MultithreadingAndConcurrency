package com.zkc.container;

import com.zkc.util.Print;
import com.zkc.util.ThreadUtil;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * CopyOnWrite（写时复制）就是在修改器对一块内存进行修改时，不直接在原有内存块上进行写操作，而是将内存复制一份，在新的内
 * 存中进行写操作，写完之后，再将原来的指针（或者引用）指向新的内存，原来的内存被回收。CopyOnWriteArrayList是写时复制思想的
 * <p>
 * 读取、遍历操作不需要同步，速度会非常快。所以，CopyOnWriteArrayList适用于读操作多、写操作相对较少的场景（读多写少）
 */
public class CopyOnWriteArrayListDemo {
	
	public static void main(String[] args) {
		List<String> notSafeList = Arrays.asList("a", "b", "c");
		//创建一个CopyOnWriteArrayList列表
		List<String> cowList = new CopyOnWriteArrayList<>(notSafeList);
		//并发执行任务目标
		ConcurrentTargetTask task = new ConcurrentTargetTask(cowList);
		for (int i = 1; i <= 10; i++) {
			new Thread(task, "线程" + i).start();
		}
		ThreadUtil.sleepSeconds(1);
	}
	
	private static class ConcurrentTargetTask implements Runnable {
		
		//并发操作的目标队列
		List<String> targetList = null;
		
		public ConcurrentTargetTask(List<String> targetList) {
			this.targetList = targetList;
		}
		
		@Override
		public void run() {
			/*
			使用CopyOnWriteArrayList容器可以在进行元素迭代的同时进行元素添加操作
			 */
			Iterator<String> iterator = targetList.iterator();
			while (iterator.hasNext()) {
				//迭代时修改列表
				String threadName = ThreadUtil.getCurThreadName();
				//往队列中添加线程名
				Print.tco("开始往同步队列中加入线程名称：" + threadName);
				targetList.add(threadName);
			}
		}
	}
}
