package com.zkc.designPattern.forkjoin;

import com.zkc.util.Print;
import org.junit.Assert;

import java.util.concurrent.*;

/**
 * ForkJoin模式先把一个大任务分解成许多个独立的子任务，然后开启多个线程并行去处理这些子任务。
 * 有可能子任务还是很大而需要进一步分解，最终得到足够小的任务
 * <p>
 * ForkJoin模式将分解出来的子任务放入双端队列中，然后几个启动线程从双端队列中获取任务并执行。
 * 子任务执行的结果放到一个队列中，各个线程从队列中获取数据，然后进行局部结果的合并，得到最终结果
 * <p>
 * 计算1～100的累加求和，可以使用ForkJoin框架完成
 */
public class ForkjoinDemo {
	
	public static void main(String[] args) throws ExecutionException, InterruptedException, TimeoutException {
		//使用ForkJoinPool调度AccumulateTask()
		ForkJoinPool pool = new ForkJoinPool();
		
		//创建一个累加任务 计算由1加到100
		AccumulateTsk task = new AccumulateTsk(1, 100);
		Future<Integer> future = pool.submit(task);
		Integer sum = future.get(1, TimeUnit.SECONDS);
		
		Print.tco("最终计算结果为：" + sum);
		//预期结果为5050
		Assert.assertTrue(sum == 5050);
	}
}
