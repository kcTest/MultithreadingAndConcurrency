package com.zkc.asyncCallback;

import com.zkc.util.Logger;

/**
 * 调用join()实现一个异步阻塞版本的泡茶
 * <p>
 * 主线程通过分别调用烧水线程和清洗线程的join()方法，等待烧水线程和清洗线程执行完成，然后执行主线程自己的泡茶操作
 * <p>
 * 主线程获取不到异步线程的返回值
 */
public class MakeTeaDemo01 {
	
	public static void main(String[] args) {
		Thread.currentThread().setName("主线程");
		
		HotWaterThead hotWaterThead = new HotWaterThead();
		WashThead washThead = new WashThead();
		hotWaterThead.start();
		washThead.start();
		//..等待烧水和清洗时 可以做点别的事情
		try {
			//合并烧水线程
			hotWaterThead.join();
			//合并清洗线程
			washThead.join();
			Logger.info("泡茶喝");
		} catch (InterruptedException e) {
			Logger.info(Thread.currentThread().getName() + " 发生异常被中断");
		}
		Logger.info(Thread.currentThread().getName() + " 运行结束.");
	}
	
	private static final int SLEEP_GAP = 500;
	
	private static class HotWaterThead extends Thread {
		
		public HotWaterThead() {
			super("** 烧水-Thread");
		}
		
		@Override
		public void run() {
			Logger.info("洗好水壶");
			Logger.info("灌上凉水");
			Logger.info("放在火上");
			try {
				Thread.sleep(SLEEP_GAP);
			} catch (InterruptedException e) {
				Logger.info(" 烧水过程发生异常 被中断");
			}
			Logger.info("水开了");
			Logger.info(" 运行结束.");
		}
	}
	
	private static class WashThead extends Thread {
		
		public WashThead() {
			super("$$ 清洗-Thread");
		}
		
		@Override
		public void run() {
			Logger.info("洗茶壶");
			Logger.info("洗茶杯");
			Logger.info("拿茶叶");
			try {
				Thread.sleep(SLEEP_GAP);
			} catch (InterruptedException e) {
				Logger.info(" 清洗过程发生异常 被中断");
			}
			Logger.info("洗完了");
			Logger.info(" 运行结束.");
		}
	}
	
}
