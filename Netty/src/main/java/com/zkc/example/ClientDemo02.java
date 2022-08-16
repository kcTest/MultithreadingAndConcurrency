package com.zkc.example;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

public class ClientDemo02 {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ClientDemo02.class);
	
	public static void main(String[] args) throws InterruptedException {
		ChannelFuture channelFuture = new Bootstrap()
				.group(new NioEventLoopGroup())
				.channel(NioSocketChannel.class)
				.handler(new ChannelInitializer<NioSocketChannel>() {
					@Override
					protected void initChannel(NioSocketChannel ch) throws Exception {
						ch.pipeline().addLast(new StringEncoder());
					}
				})
				/*异步非阻塞 此处继续直接向下运行  由NioEventLoopGroup中的线程负责去连接*/
				.connect(new InetSocketAddress(8080));

//		/*1、阻塞住 直到连接建立成功*/
//		channelFuture.sync();
//		/* channelFuture的sync()不执行的情况 此处连接可能并未建立成功 */
//		Channel channel = channelFuture.channel();
//		LOGGER.debug("status:{}", channel);
//		Scanner scanner = new Scanner(System.in);
//		while (scanner.hasNext()) {
//			String msg = scanner.nextLine();
//			channel.writeAndFlush(msg);
//		}
		
		/*2、使用addListener(ChannelFuture)监听Future对象异步操作(此处为connect)的返回结果 */
		channelFuture.addListener(new ChannelFutureListener() {
			@Override
			public void operationComplete(ChannelFuture future) throws Exception {
				/*连接完成后会调用*/
				Channel channel = future.channel();
				LOGGER.debug("status:{}", channel);
			}
		});
		
	}
	
}
