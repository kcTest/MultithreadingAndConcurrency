package com.zkc.example.stickyAndHalf;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * 4、 基于长度字段的解码器 发送的消息分为多个部分 根据字段信息提取消息内容
 * lengthFieldOffset:消息长度字段的偏移量
 * lengthFieldLength:消息长度字段的长度
 * lengthAdjustment:消息长度字段 与 实际消息 之间的 其它字段 的长度
 * initialBytesToStrip:从头开始要剔除的字节长度，根据实际情况选择是否剔除实际消息之前某些字段
 * 【-------------------initialBytesToStrip------------------】
 * 【lengthFieldOffset】【lengthFieldLength】【lengthAdjustment】【实际消息】
 */
@Slf4j
public class ServerDemo08 {
	
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
							/*约定好消息发送格式*/
							ch.pipeline().
//									addLast(new LengthFieldBasedFrameDecoder(1024, 0, 4, 0, 4))
		addLast(new LengthFieldBasedFrameDecoder(1024, 4, 4, 4, 12))
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
