package com.zkc.netty.stickyAndHalf;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.FixedLengthFrameDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * 2、定长解码器
 */
@Slf4j
public class ServerDemo06 {
	
	public static void main(String[] args) {
		NioEventLoopGroup acceptGroup = new NioEventLoopGroup();
		NioEventLoopGroup ioGroup = new NioEventLoopGroup();
		
		try {
			ChannelFuture channelFuture = new ServerBootstrap()
					.group(acceptGroup, ioGroup)
					.channel(NioServerSocketChannel.class)
					.childHandler(new ChannelInitializer<NioSocketChannel>() {
						@Override
						protected void initChannel(NioSocketChannel ch) throws Exception {
							ch.pipeline()
									/*双方约定每条消息最大长度 先添加解码处理器 ;将接收的数据按10个字节拆分解码 不足部分等下次够10个时再处理*/
									.addLast(new FixedLengthFrameDecoder(10))
									.addLast(new LoggingHandler(LogLevel.DEBUG));
						}
					})
					.bind(8080)
					.sync();
			channelFuture.channel().closeFuture().sync();
		} catch (InterruptedException e) {
			log.error("server error: ", e);
		} finally {
			acceptGroup.shutdownGracefully();
			ioGroup.shutdownGracefully();
		}
		
	}
}
