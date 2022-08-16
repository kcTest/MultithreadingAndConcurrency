package com.zkc.example;

import io.netty.channel.EventLoop;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.concurrent.DefaultPromise;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutionException;

/**
 * Future-juc VS Future-netty Vs Promise-netty
 */
public class PromiseDemoNetty {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(PromiseDemoNetty.class);
	
	public static void main(String[] args) throws ExecutionException, InterruptedException {
		//1、创建EventLoop
		EventLoopGroup group = new NioEventLoopGroup();
		EventLoop eventLoop = group.next();
		//2、可主动创建的Promise对象 可手动填充结果
		DefaultPromise<Integer> promise = new DefaultPromise<>(eventLoop);
		new Thread(() -> {
			try {
				LOGGER.debug("子线程模拟计算...");
				Thread.sleep(3000);
				//3、子线程填充正确结果或异常结果
				promise.setSuccess(5);
			} catch (InterruptedException e) {
				e.printStackTrace();
				//3、子线程填充正确结果或异常结果
				promise.setFailure(e);
			}
		}).start();
		LOGGER.debug("主线程等待获取结果...");
		//4、get同步获取或通过addListener异步获取
		LOGGER.debug("结果={}", promise.get());
	}
}
