package com.zkc.netty;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;

/**
 * Future-juc VS Future-netty Vs Promise-netty
 */
public class FutureDemoJuc {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(FutureDemoJuc.class);
	
	public static void main(String[] args) throws ExecutionException, InterruptedException {
		//1、创建线程池
		ExecutorService pool = Executors.newFixedThreadPool(2);
		//2、提交任务
		Future<Integer> future = pool.submit(new Callable<Integer>() {
			@Override
			public Integer call() throws Exception {
				LOGGER.debug("线程池线程模拟计算...");
				Thread.sleep(3000);
				return 5;
			}
		});
		//3、同步获取结果
		LOGGER.debug("主线程同步等待获取结果...");
		LOGGER.debug("结果={}", future.get());
		pool.shutdown();
	}
}
