package com.zkc.designPattern.masterWorker;

import com.zkc.util.Print;
import com.zkc.util.ThreadUtil;

import java.util.concurrent.TimeUnit;

/**
 * 任务调度和执行分离，调度任务的角色为Master，执行任务的角色为 Worker，Master负责接收、分配任务和合并（Merge）任务结果，
 * Worker负责执行任务。Master-Worker模式是一种归并类型的模式
 * <p>
 * 执行N个任务，Master持有workerCount个Worker，并且负责接收任务，然后分发给Worker，最后在回调函数中对Worker的结果进行归并求和
 */
public class MasterWorkerDemo {
	
	public static void main(String[] args) {
		//创建Master 包含4个worker 并启动Master的执行线程
		Master<SimpleTask, Integer> master = new Master<>(4);
		
		//定期向master提交任务  延迟2s执行第一个任务 之后每隔2s再执行下一个任务
		ThreadUtil.scheduleAtFixRate(() -> {
			master.submit(new SimpleTask());
		}, 2, TimeUnit.SECONDS);
		
		//定期从master提取结果  返回为id的累加
		ThreadUtil.scheduleAtFixRate(() -> {
			master.printResult();
		}, 5, TimeUnit.SECONDS);
	}
	
	/**
	 * 简单任务
	 */
	private static class SimpleTask extends Task<Integer> {
		@Override
		protected Integer doExecute() {
			Print.tco("task " + getId() + " is done");
			return getId();
		}
	}
}
