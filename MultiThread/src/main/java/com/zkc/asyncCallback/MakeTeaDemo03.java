package com.zkc.asyncCallback;

import com.google.common.util.concurrent.*;
import com.zkc.util.Print;
import com.zkc.util.ThreadUtil;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.concurrent.*;

/**
 * 使用Guava实现泡茶喝的实例
 *
 * <p>
 * Guava异步回调的流程如下：
 * （1）实现Java的Callable接口，创建异步执行逻辑。还有一种情况，如果不需要返回值，异步执行逻辑也可以实现Runnable接口。
 * （2）创建Guava线程池。
 * （3）将（1）创建的Callable/Runnable异步执行逻辑的实例提交到Guava线程池，从而获取ListenableFuture异步任务实例。
 * （4）创建FutureCallback回调实例，通过Futures.addCallback将回调实例绑定到ListenableFuture异步任务上。
 * 完成以上4步，当Callable/Runnable异步执行逻辑完成后，就会回调FutureCallback实例的回调方法onSuccess()/onFailure()
 * <p>
 *
 * <p>Guava异步回调和Java异步调用的区别：
 * （1）FutureTask是主动调用的模式，“调用线程”主动获得异步结果，在获取异步结果时处于阻塞状态，并且会一直阻塞，直到拿到异步线程的结果。
 * （2）Guava是异步回调模式，“调用线程”不会主动获得异步结果，而是准备好回调函数，并设置好回调钩子，执行回调函数的并不
 * 是“调用线程”自身，回调函数的执行者是“被调用线程”，“调用线程”在执行完自己的业务逻辑后就已经结束了，当回调函数被执行时，“调用线程”可能已经结束很久了
 */
public class MakeTeaDemo03 {
	
	public static void main(String[] args) {
		Thread.currentThread().setName("主线程");
		DrinkAction drinkAction = new DrinkAction();
		
		//烧水的业务逻辑
		Callable<Boolean> hotWaterAction = new HotWaterAction();
		//清洗的业务逻辑
		Callable<Boolean> washAction = new WashAction();
		
		//创建java线程池
		ExecutorService jPool = Executors.newFixedThreadPool(10);
		//包装java线程 构造guava线程池
		ListeningExecutorService gPool = MoreExecutors.listeningDecorator(jPool);
		
		//定义烧水的回调钩子
		FutureCallback<Boolean> hotWaterHook = new FutureCallback<Boolean>() {
			@Override
			public void onSuccess(Boolean b) {
				if (b) {
					drinkAction.waterOk = true;
					//线程池内可用线程去执行回调方法
					drinkAction.drinkTea();
				}
			}
			
			@Override
			public void onFailure(Throwable t) {
				Print.tcfo("烧水失败 不能泡茶喝");
			}
		};
		//启动烧水线程
		ListenableFuture<Boolean> hotWaterFuture = gPool.submit(hotWaterAction);
		//设置烧水任务的回调钩子  
		Futures.addCallback(hotWaterFuture, hotWaterHook, gPool);
		
		//启动清洗线程
		ListenableFuture<Boolean> washFuture = gPool.submit(washAction);
		//使用匿名实作为清洗任务的回调钩子
		Futures.addCallback(washFuture, new FutureCallback<Boolean>() {
			@Override
			public void onSuccess(@Nullable Boolean b) {
				if (b) {
					drinkAction.cupOk = true;
					//线程池内可用线程去执行回调方法
					drinkAction.drinkTea();
				}
			}
			
			@Override
			public void onFailure(Throwable t) {
				Print.tcfo("清洗失败 不能泡茶喝");
			}
		}, gPool);
		
		Print.tcfo("干点其它事情");
		ThreadUtil.sleepSeconds(1);
		Print.tcfo("主线程运行结束.");
		/*
[pool-1-thread-1|MakeTeaDemo03$HotWaterAction:call]: 烧水  运行结束.
[pool-1-thread-2|MakeTeaDemo03$WashAction:call]: 清洗 运行结束.
[pool-1-thread-3|MakeTeaDemo03$DrinkAction:drinkTea]: 泡茶喝，茶喝完
[pool-1-thread-4|MakeTeaDemo03$DrinkAction:drinkTea]: 泡茶喝，茶喝完
		 */
	}
	
	private static final int SLEEP_GAP = 3000;
	
	private static class HotWaterAction implements Callable<Boolean> {
		
		/**
		 * 返回了异步线程的执行结果
		 */
		@Override
		public Boolean call() {
			Print.tcfo("洗好水壶");
			Print.tcfo("灌上凉水");
			Print.tcfo("放在火上");
			try {
				Thread.sleep(SLEEP_GAP);
			} catch (InterruptedException e) {
				Print.tcfo(" 烧水过程发生异常 被中断");
				return false;
			}
			Print.tcfo("水开了");
			Print.tcfo("烧水  运行结束.");
			return true;
		}
	}
	
	private static class WashAction implements Callable<Boolean> {
		
		@Override
		public Boolean call() {
			Print.tcfo("洗茶壶");
			Print.tcfo("洗茶杯");
			Print.tcfo("拿茶叶");
			try {
				Thread.sleep(SLEEP_GAP);
			} catch (InterruptedException e) {
				Print.tcfo(" 清洗过程发生异常 被中断");
				return false;
			}
			Print.tcfo("洗完了");
			Print.tcfo("清洗 运行结束.");
			return true;
		}
	}
	
	private static class DrinkAction {
		boolean waterOk = false;
		boolean cupOk = false;
		
		//泡茶喝 回调方法
		public void drinkTea() {
			if (waterOk && cupOk) {
				Print.tcfo("泡茶喝，茶喝完");
			}
		}
	}
}
