package com.zkc.asyncCallback;

import com.zkc.util.Print;
import com.zkc.util.ThreadUtil;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;

/**
 * 使用CompletableFuture实现泡茶喝实例
 */
public class MakeTeaDemo04 {
	
	public static void main(String[] args) {
		//任务1 洗水壶->烧开水
		CompletableFuture<Boolean> hotWaterFuture = CompletableFuture.supplyAsync(() -> {
			Print.tco("洗好水壶");
			Print.tco("烧开水");
			//线程睡眠 代表烧水
			ThreadUtil.sleepMilliseconds(SLEEP_GAP);
			Print.tco("水开了");
			return true;
		});
		//任务2 洗茶壶->洗茶杯->拿茶叶
		CompletableFuture<Boolean> washFuture = CompletableFuture.supplyAsync(() -> {
			Print.tco("洗茶壶");
			Print.tco("洗茶杯");
			Print.tco("拿茶叶");
			//线程睡眠 代表清洗
			ThreadUtil.sleepMilliseconds(SLEEP_GAP);
			Print.tco("清洗完成");
			return true;
		});
		CompletableFuture<String> drinkFuture = hotWaterFuture.thenCombine(washFuture, new BiFunction<Boolean, Boolean, String>() {
			@Override
			public String apply(Boolean hotWaterOk, Boolean washOk) {
				if (hotWaterOk && washOk) {
					Print.tcfo("泡茶喝,茶喝完");
					return "泡茶喝成功";
				}
				return "泡茶喝失败";
			}
		});
		//等待任务3执行结果
		Print.tco(drinkFuture.join());
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
