package com.zkc.example.stickyAndHalf;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
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
import java.nio.charset.StandardCharsets;

/**
 * 3、基于长度字段的解码器
 */
@Slf4j
public class ClientDemo08 {
	
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
											
											/*先写入实际消息长度 再写入内容*/
											ByteBuf buf = ByteBufAllocator.DEFAULT.buffer();
											byte[] actualMsg = "hello world".getBytes(StandardCharsets.UTF_8);
											int length = actualMsg.length;
											int before = 1;
											int after = 2;
											buf.writeInt(before);
											buf.writeInt(length);
											buf.writeInt(after);
											buf.writeBytes(actualMsg);
											
											actualMsg = "Returns the EventExecutor.".getBytes(StandardCharsets.UTF_8);
											length = actualMsg.length;
											before = 3;
											after = 4;
											buf.writeInt(before);
											buf.writeInt(length);
											buf.writeInt(after);
											buf.writeBytes(actualMsg);
											
											actualMsg = "The resultant byte array".getBytes(StandardCharsets.UTF_8);
											length = actualMsg.length;
											before = 5;
											after = 6;
											buf.writeInt(before);
											buf.writeInt(length);
											buf.writeInt(after);
											buf.writeBytes(actualMsg);
											
											ctx.writeAndFlush(buf);
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
