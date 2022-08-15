package com.zkc.netty.stickyAndHalf;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;

/**
 * 3、分隔符
 */
@Slf4j
public class ServerDemo07 {
	
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
							/*分隔符如果在指定长度内没有出现会出错*/
							ByteBuf delimiter = Unpooled.copiedBuffer("|".getBytes(StandardCharsets.UTF_8));
							ch.pipeline().
									addLast(new DelimiterBasedFrameDecoder(1024, delimiter))
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
