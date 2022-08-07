package com.zkc.asyncCallback;

import com.zkc.util.Logger;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

/**
 * 使用FutureTask类和Callable接口进行泡茶喝的实战
 * <p>
 * 异步阻塞模式  获取异步结果时，主线程也会被阻塞
 * <p>
 * 主动调用是一种阻塞式调用，它是一种单向调用，“调用方”要等待“被调用方”执行完毕才返回。
 * 如果“被调用方”的执行时间很长，那么“调用方”线程需要阻塞很长一段时间。
 * 如何将主动调用的方向进行反转呢？这就是异步回调。回调是一种反向的调用模式，也就是说，被调用方在执行完成后，会反向执行“调用方”所设置的钩子方法
 * 执行回调方法的具体线程已经不再是调用方的线程（如示例中的泡茶线程），而是变成了异步的被调用方的线程（如烧水线程）
 */
public class MakeTeaDemo02 {
	
	public static void main(String[] args) {
		Thread.currentThread().setName("主线程");
		
		HotWaterAction hotWaterAction = new HotWaterAction();
		FutureTask<Boolean> hotWaterTask = new FutureTask<>(hotWaterAction);
		//通过FutureTask实例创建新的线程
		Thread hotWaterThread = new Thread(hotWaterTask, "** 烧水-Thread");
		
		WashAction washAction = new WashAction();
		FutureTask<Boolean> washTask = new FutureTask<>(washAction);
		//通过FutureTask实例创建新的线程
		Thread washThread = new Thread(washTask, "$$ 清洗-Thread");
		
		hotWaterThread.start();
		washThread.start();
		
		//..等待烧水和清洗时 可以做点别的事情
		try {
			//异步阻塞模式  获取异步结果时，主线程也会被阻塞
			Boolean waterOk = hotWaterTask.get();
			Boolean cupOk = washTask.get();
			if (waterOk && cupOk) {
				Logger.info("泡茶喝");
			} else if (!waterOk) {
				Logger.info("烧水失败 不能泡茶喝");
			} else {
				Logger.info("清洗失败 不能泡茶喝");
			}
		} catch (InterruptedException e) {
			Logger.info(Thread.currentThread().getName() + " 发生异常被中断");
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		Logger.info(Thread.currentThread().getName() + " 运行结束.");
	}
	
	private static final int SLEEP_GAP = 500;
	
	private static class HotWaterAction implements Callable<Boolean> {
		
		/**
		 * 返回了异步线程的执行结果
		 */
		@Override
		public Boolean call() {
			Logger.info("洗好水壶");
			Logger.info("灌上凉水");
			Logger.info("放在火上");
			try {
				Thread.sleep(SLEEP_GAP);
			} catch (InterruptedException e) {
				Logger.info(" 烧水过程发生异常 被中断");
				return false;
			}
			Logger.info("水开了");
			Logger.info(" 运行结束.");
			return true;
		}
	}
	
	private static class WashAction implements Callable<Boolean> {
		
		@Override
		public Boolean call() {
			Logger.info("洗茶壶");
			Logger.info("洗茶杯");
			Logger.info("拿茶叶");
			try {
				Thread.sleep(SLEEP_GAP);
			} catch (InterruptedException e) {
				Logger.info(" 清洗过程发生异常 被中断");
				return false;
			}
			Logger.info("洗完了");
			Logger.info(" 运行结束.");
			return true;
		}
	}
	
}
