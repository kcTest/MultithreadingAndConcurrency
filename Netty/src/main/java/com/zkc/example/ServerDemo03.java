package com.zkc.example;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

/**
 * handler 调用顺序
 */
public class ServerDemo03 {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ServerDemo03.class);
	
	public static void main(String[] args) {
		
		new ServerBootstrap()
				.group(new NioEventLoopGroup())
				.channel(NioServerSocketChannel.class)
				.childHandler(new ChannelInitializer<NioSocketChannel>() {
					@Override
					protected void initChannel(NioSocketChannel ch) throws Exception {
						ChannelPipeline pipeline = ch.pipeline();
						/* 
						默认提前添加head、tail handler，自定义handler在head-tail之间。
						head<->COH4<->CIH1<->CIH2<->CIH3<->COH1<->COH2<->COH3<->tail
						入站调用顺序head->CIH1->CIH2->CIH3
						*/
						pipeline.addLast("COH4", new ChannelOutboundHandlerAdapter() {
							
							@Override
							public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
								LOGGER.debug("COH4 data：{}", msg);
								super.write(ctx, msg, promise);
							}
						});
						pipeline.addLast("CIH1", new ChannelInboundHandlerAdapter() {
							
							@Override
							public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
								LOGGER.debug("CIH1 data：{}", msg);
								super.channelRead(ctx, msg);
								/*向客户端NioSocketChannel写入数据 触发后面的出站处理器的调用 出站调用顺序tail->COH3->COH2->COH1->COH4 */
								ch.writeAndFlush(ctx.alloc().buffer().writeBytes("data from CIH1".getBytes(StandardCharsets.UTF_8)));
							}
						});
						pipeline.addLast("CIH2", new ChannelInboundHandlerAdapter() {
							
							@Override
							public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
								LOGGER.debug("CIH2 data：{}", msg);
								/*如果不传递入站处理器h2与h3会断开*/
								super.channelRead(ctx, msg);
								/* 反向查找出站处理器 不会触发后面的出站处理器调用 在CIH1前添加出站处理器COH4此时可以找到 出站调用顺序 ->COH4 ,COH4被调用两次*/
								ctx.writeAndFlush(ctx.alloc().buffer().writeBytes("data from CIH2".getBytes(StandardCharsets.UTF_8)));
							}
						});
						pipeline.addLast("CIH3", new ChannelInboundHandlerAdapter() {
							
							@Override
							public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
								LOGGER.debug("CIH3 data：{}", msg);
								/*最后一个入站处理器 不需要再调用下一个入站处理器*/
//								super.channelRead(ctx, msg);
							}
						});
						/*
						head<->COH4<->CIH1<->CIH2<->CIH3<->COH1<->COH2<->COH3<->tail
						出站主要重写write 服务端向与客户端相关的channel写入数据时才会触发 
						*/
						pipeline.addLast("COH1", new ChannelOutboundHandlerAdapter() {
							
							@Override
							public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
								LOGGER.debug("COH1 data：{}", msg);
								super.write(ctx, msg, promise);
							}
						});
						pipeline.addLast("COH2", new ChannelOutboundHandlerAdapter() {
							
							@Override
							public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
								LOGGER.debug("COH2 data：{}", msg);
								super.write(ctx, msg, promise);
							}
						});
						pipeline.addLast("COH3", new ChannelOutboundHandlerAdapter() {
							
							@Override
							public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
								LOGGER.debug("COH3 data：{}", msg);
								super.write(ctx, msg, promise);
							}
						});
					}
				})
				.bind(new InetSocketAddress(8080));
		
	}
}
