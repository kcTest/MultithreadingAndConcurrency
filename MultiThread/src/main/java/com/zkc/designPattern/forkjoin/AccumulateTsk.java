package com.zkc.designPattern.forkjoin;

import com.zkc.util.Print;

import java.util.concurrent.RecursiveTask;

public class AccumulateTsk extends RecursiveTask<Integer> {
	
	private static final int THRESHOLD = 2;
	
	/**
	 * 累加的起始编号
	 */
	private int start;
	
	/**
	 * 累加的结束编号
	 */
	private int end;
	
	public AccumulateTsk(int start, int end) {
		this.start = start;
		this.end = end;
	}
	
	
	@Override
	protected Integer compute() {
		int sum = 0;
		//判断任务的规模 若规模足够小可以直接计算
		boolean canCompute = (end - start) <= THRESHOLD;
		/*
		若当前的计算规模没有大于THRESHOLD，则直接计算（这里为求和）
		 */
		if (canCompute) {
			//直接计算并返回结果 Recursive结束
			for (int i = start; i <= end; i++) {
				sum += i;
			}
			Print.tco("执行任务,计算" + start + "到" + end + "的和，结果是：" + sum);
		} else {
			/*		
			若当前的计算规模（这里为求和的数字个数）大于THRESHOLD，当前子任务需要进一步分解，
            就需要等待所有的子任务执行完毕、然后对各个分解结果求和。
            如果一个任务分解为多个子任务（含两个），就依次调用每个子任务的fork()方法执行子任务，然后依次调用每个子任务的join()方法合并执行结果
			 */
			//任务过大 需要切割  继续Recursive递归计算
			Print.tco("切割任务：将" + start + "到" + end + "的和一分为二");
			int middle = (start + end) >> 1;
			//切割成俩个子任务
			AccumulateTsk lTask = new AccumulateTsk(start, middle);
			AccumulateTsk rTask = new AccumulateTsk(middle + 1, end);
			//依次调用每个任务的join方法合并执行结果
			lTask.fork();
			rTask.fork();
			//等待子任务完成 依次调用每个子任务的join方法合并执行结果
			int leftResult = lTask.join();
			int rightResult = rTask.join();
			//合并子任务执行结果
			sum = leftResult + rightResult;
		}
		return sum;
	}
}
