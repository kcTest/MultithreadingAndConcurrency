package com.zkc.netty;

import io.netty.channel.DefaultEventLoop;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class EventLoopDemo {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(EventLoopDemo.class);
	
	public static void main(String[] args) {
		/*1、创建事件循环组 */
		
		//DefaultEventLoop: 处理 普通任务 定时任
		EventLoopGroup group1 = new DefaultEventLoop();
		//NioEventLoopGroup: 处理 I/O事件 普通任务 定时任务
		EventLoopGroup group2 = new NioEventLoopGroup(2);
		/* 2、获取下一个事件循环对象 负载均衡 平均分配 */
		System.out.println(group2.next());
		System.out.println(group2.next());
		System.out.println(group2.next());
		System.out.println(group2.next());
		
		LOGGER.info("START");
//		/* 3、执行普通任务 */
//		group2.next().submit(() -> {
//			LockSupport.parkNanos(1000 * 1000 * 1000);
//			LOGGER.debug("执行普通任务");
//		});
		
		/* 4、执行定时任务*/
		group2.next().scheduleAtFixedRate(() -> {
			LOGGER.debug("执行定时任务");
		}, 0, 2, TimeUnit.SECONDS);
		
		
		LOGGER.info("END");
	}
}
