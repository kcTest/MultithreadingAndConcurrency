package com.zkc.netty.stickyAndHalf;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

/**
 * 3、分隔符 通过检查每个字节确定消息边界 效率低
 */
@Slf4j
public class ClientDemo07 {
	
	public static void main(String[] args) {
		NioEventLoopGroup ioGroup = new NioEventLoopGroup();
		try {
			ChannelFuture channelFuture = new Bootstrap()
					.group(ioGroup)
					.channel(NioSocketChannel.class)
					.handler(new ChannelInitializer<NioSocketChannel>() {
						@Override
						protected void initChannel(NioSocketChannel ch) throws Exception {
							ch.pipeline()
									.addLast(new LoggingHandler(LogLevel.DEBUG))
									.addLast(new ChannelInboundHandlerAdapter() {
										@Override
										public void channelActive(ChannelHandlerContext ctx) throws Exception {
											sendFixedLenMsg(ctx);
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
	
	/**
	 * 单次发送一条随机长度的消息 每条消息最大长度N，消息末尾位置填充一个分隔符
	 */
	private static void sendFixedLenMsg(ChannelHandlerContext ctx) {
		for (int i = 0; i < 5; i++) {
			ByteBuf buf = ctx.alloc().buffer();
			int msgLen = (int) (Math.random() * 10 - 1) + 1;
			for (int j = 0; j < msgLen; j++) {
				buf.writeByte((int) (Math.random() * 26) + 97);
			}
			if (msgLen < 10) {
				buf.writeByte('|');
			}
			ctx.writeAndFlush(buf);
		}
	}
	
}
