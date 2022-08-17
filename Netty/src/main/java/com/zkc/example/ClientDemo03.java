package com.zkc.example;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.Scanner;

/**
 * close gracefully
 * NIOEventLoop是非守护线程 运行之后 不会主动退出 只有调用shutdown方法才会退出
 */
public class ClientDemo03 {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ClientDemo03.class);
	
	public static void main(String[] args) throws InterruptedException {
	   /* 
	   最后线程不能完全停止 
	   需要将NioEventLoopGroup提取出来 在channel关闭后接着关闭EventLoopGroup 
	   */
		NioEventLoopGroup group = new NioEventLoopGroup();
		ChannelFuture channelFuture = new Bootstrap()
//				.group(new NioEventLoopGroup())
				.group(group)
				.channel(NioSocketChannel.class)
				.handler(new ChannelInitializer<NioSocketChannel>() {
					@Override
					protected void initChannel(NioSocketChannel ch) throws Exception {
						ch.pipeline()
								/*打印事件 先添加*/
								.addLast(new LoggingHandler(LogLevel.DEBUG))
								.addLast(new StringEncoder());
					}
				})
				.connect(new InetSocketAddress(8080));
		
		Channel channel = channelFuture.sync().channel();
		LOGGER.debug("{}", channel);
		new Thread(() -> {
			Scanner scanner = new Scanner(System.in);
			while (scanner.hasNext()) {
				String msg = scanner.nextLine();
				if ("exit".equals(msg)) {
//					/*异步操作  不能保证善后工作执行时channel已经关闭*/
//					channel.close();
//					LOGGER.debug("模拟关闭之后的善后工作...");
					channel.close();
					break;
				}
				channel.writeAndFlush(msg);
			}
		}, "Input").start();

//		/* 1-1、同步处理  sync阻塞 等待输入exit后才会执行善后工作 */
//		ChannelFuture closeFuture = channel.closeFuture();
//		LOGGER.debug("wait closing......");
//		closeFuture.sync();
//		LOGGER.debug("模拟关闭之后的善后工作...");
//		group.shutdownGracefully();
		
		/* 1-2、异步处理  sync阻塞 等待输入exit后才会执行善后工作 */
		ChannelFuture closeFuture = channel.closeFuture();
		closeFuture.addListener(new ChannelFutureListener() {
			@Override
			public void operationComplete(ChannelFuture future) throws Exception {
				//执行关闭的线程会来执行此处善后工作
				LOGGER.debug("模拟关闭之后的善后工作...");
				/*2、优雅关闭EventLoopGroup中的线程*/
				group.shutdownGracefully();
			}
		});
		
	}
	
}
