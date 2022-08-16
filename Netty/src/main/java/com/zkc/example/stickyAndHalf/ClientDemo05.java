package com.zkc.example.stickyAndHalf;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

/**
 * 1、短连接 不能解决半包问题
 */
@Slf4j
public class ClientDemo05 {
	
	public static void main(String[] args) {
		for (int i = 0; i < 10; i++) {
			sendMsg();
		}
	}
	
	private static void sendMsg() {
		NioEventLoopGroup ioGroup = new NioEventLoopGroup();
		try {
			ChannelFuture channelFuture = new Bootstrap()
					.group(ioGroup)
					.channel(NioSocketChannel.class)
					.handler(new ChannelInitializer<NioSocketChannel>() {
						@Override
						protected void initChannel(NioSocketChannel ch) throws Exception {
							ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {
								@Override
								public void channelActive(ChannelHandlerContext ctx) throws Exception {
									/*设置发送超过接收方缓冲区的大小 出现半包问题 16+1*/
									ByteBuf buf = ctx.alloc().buffer(17);
									buf.writeBytes(new byte[]{0, 1, 2, 3, 4, 5, 6, 7, 8,
											9, 10, 11, 12, 13, 14, 15, 16});
									ctx.writeAndFlush(buf);
									/* 发送后断开  每次断开接收方结束本次接收 相当于确定了一个消息边界 不会出现消息黏包问题*/
									ctx.channel().close();
								}
							});
						}
					})
					.connect(new InetSocketAddress("127.0.0.1", 8080))
					.sync();
			
			channelFuture.channel().closeFuture().sync();
		} catch (InterruptedException e) {
			log.error("client error: ");
		} finally {
			ioGroup.shutdownGracefully();
		}
	}
	
}
