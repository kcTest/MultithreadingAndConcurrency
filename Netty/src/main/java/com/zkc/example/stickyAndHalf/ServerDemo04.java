package com.zkc.example.stickyAndHalf;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * 现象
 */
@Slf4j
public class ServerDemo04 {
	
	public static void main(String[] args) {
		NioEventLoopGroup acceptGroup = new NioEventLoopGroup();
		NioEventLoopGroup ioGroup = new NioEventLoopGroup();
		
		try {
			ChannelFuture channelFuture = new ServerBootstrap()
					/* 调整接收方接收缓冲器（滑动窗口）大小 小于发送方单次发送大小 ；服务器每次最多可以接收15个字节而不用等待响发送方响应 ；演示半包现象*/
					.option(ChannelOption.SO_RCVBUF,15)
					/*netty的接收缓冲区(byteBuf)大小最小16 根据发送方大小自动扩容 ；不调整演示黏包现象*/
					.group(acceptGroup, ioGroup)
					.channel(NioServerSocketChannel.class)
					.childHandler(new ChannelInitializer<NioSocketChannel>() {
						@Override
						protected void initChannel(NioSocketChannel ch) throws Exception {
							ch.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG));
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
