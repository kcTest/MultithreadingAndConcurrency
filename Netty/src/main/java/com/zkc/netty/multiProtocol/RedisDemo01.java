package com.zkc.netty.multiProtocol;

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

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

/**
 * 按redis的协议向redis服务器发送指令
 * <p>整个命令看成数组
 * <p>[set key value]
 * <p> *数组元素个数\r\n
 * <p> $单个指令长度\r\n
 * <p> 具体指令\r\n
 */
public class RedisDemo01 {
	
	public static void main(String[] args) {
		NioEventLoopGroup group = new NioEventLoopGroup();
		try {
			ChannelFuture channelFuture = new Bootstrap()
					.group(group)
					.channel(NioSocketChannel.class)
					.handler(new ChannelInitializer<NioSocketChannel>() {
						@Override
						protected void initChannel(NioSocketChannel ch) throws Exception {
							ch.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG))
									.addLast(new ChannelInboundHandlerAdapter() {
										
										@Override
										public void channelActive(ChannelHandlerContext ctx) throws Exception {
											byte[] line = new byte[]{13, 10};
											ByteBuf buf = ByteBufAllocator.DEFAULT.buffer();
											
											buf.writeBytes("*2".getBytes(StandardCharsets.UTF_8));
											buf.writeBytes(line);
											buf.writeBytes("$4".getBytes(StandardCharsets.UTF_8));
											buf.writeBytes(line);
											buf.writeBytes("auth".getBytes(StandardCharsets.UTF_8));
											buf.writeBytes(line);
											buf.writeBytes("$9".getBytes(StandardCharsets.UTF_8));
											buf.writeBytes(line);
											buf.writeBytes("【密码】".getBytes(StandardCharsets.UTF_8));
											buf.writeBytes(line);
											
											buf.writeBytes("*3".getBytes(StandardCharsets.UTF_8));
											buf.writeBytes(line);
											buf.writeBytes("$3".getBytes(StandardCharsets.UTF_8));
											buf.writeBytes(line);
											buf.writeBytes("set".getBytes(StandardCharsets.UTF_8));
											buf.writeBytes(line);
											buf.writeBytes("$8".getBytes(StandardCharsets.UTF_8));
											buf.writeBytes(line);
											buf.writeBytes("nettyMsg".getBytes(StandardCharsets.UTF_8));
											buf.writeBytes(line);
											buf.writeBytes(("$" + "按redis的协议向redis服务器发送指令".getBytes(StandardCharsets.UTF_8).length).getBytes(StandardCharsets.UTF_8));
											buf.writeBytes(line);
											buf.writeBytes("按redis的协议向redis服务器发送指令".getBytes(StandardCharsets.UTF_8));
											buf.writeBytes(line);
											
											ctx.writeAndFlush(buf);
										}
										
										@Override
										public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
											/*接收redis响应信息*/
											ByteBuf buf = (ByteBuf) msg;
											System.out.println(buf.toString(StandardCharsets.UTF_8));
										}
									});
						}
					})
					.connect(new InetSocketAddress("centos002", 5379))
					.sync();
			
			channelFuture.channel().closeFuture().sync();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			group.shutdownGracefully();
		}
		
	}
}
