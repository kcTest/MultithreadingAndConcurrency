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
 * 现象
 */
@Slf4j
public class ClientDemo04 {
	
	public static void main(String[] args) {
		NioEventLoopGroup ioGroup = new NioEventLoopGroup();
		try {
			ChannelFuture channelFuture = new Bootstrap()
					.group(ioGroup)
					.channel(NioSocketChannel.class)
					.handler(new ChannelInitializer<NioSocketChannel>() {
						@Override
						protected void initChannel(NioSocketChannel ch) throws Exception {
							ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {
								/**
								 * 连接建立后触发
								 */
								@Override
								public void channelActive(ChannelHandlerContext ctx) throws Exception {
									/*出现类似半包15+1 黏包15+15现象*/
									for (int i = 0; i < 10; i++) {
										ByteBuf buf = ctx.alloc().buffer(16);
										buf.writeBytes(new byte[]{0, 1, 2, 3, 4, 5, 6, 7, 8,
												9, 10, 11, 12, 13, 14, 15,});
										ctx.writeAndFlush(buf);
									}
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
