package com.zkc.example;

import io.netty.channel.EventLoop;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

/**
 * Future-juc VS Future-netty Vs Promise-netty
 */
public class FutureDemoNetty {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(FutureDemoNetty.class);
	
	public static void main(String[] args) throws ExecutionException, InterruptedException {
		//1、创建EventLoop
		NioEventLoopGroup group = new NioEventLoopGroup();
		EventLoop eventLoop = group.next();
		//2、提交任务
		Future<Integer> future = eventLoop.submit(new Callable<Integer>() {
			@Override
			public Integer call() throws Exception {
				LOGGER.debug("EventLoop线程池线程模拟计算...");
				Thread.sleep(3000);
				return 5;
			}
		});
		//3、get同步获取或通过addListener异步获取
		future.addListener(new GenericFutureListener<Future<? super Integer>>() {
			@Override
			public void operationComplete(Future<? super Integer> future) throws Exception {
				LOGGER.debug("EventLoop线程池线程异步接收结果={}", future.getNow());
				group.shutdownGracefully();
			}
		});
	}
}
