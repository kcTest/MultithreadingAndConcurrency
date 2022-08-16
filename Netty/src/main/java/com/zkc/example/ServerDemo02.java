package com.zkc.example;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.DefaultEventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;

public class ServerDemo02 {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ServerDemo02.class);
	
	public static void main(String[] args) {
		 /*	 
		 2、将EventLoopGroup再次细分，比如让某些EventLoop专门执行耗时任务 不与其它类型的混用 防止影响轻量级任务的执行
            创建独立的EventLoopGroup 
         */
		DefaultEventLoopGroup group = new DefaultEventLoopGroup();
		new ServerBootstrap()
				/*
				  一个客户端对应的channel始终由同一个EventLoop处理
				  1、根据职责划分EventLoopGroup
				  第一个负责服务端ServerSocketChannel上的accept事件处理 只有一个 与NioServerSocketChannel绑定
				  第二个负责客户端SocketChannel上的数据读写 可以设置多个
				  */
				.group(new NioEventLoopGroup(), new NioEventLoopGroup(2))
				.channel(NioServerSocketChannel.class)
				.childHandler(new ChannelInitializer<NioSocketChannel>() {
					@Override
					protected void initChannel(NioSocketChannel ch) throws Exception {
						ch.pipeline().
								addLast("handler01", new ChannelInboundHandlerAdapter() {
									@Override
									public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
										ByteBuf buf = (ByteBuf) msg;
										LOGGER.debug(buf.toString(StandardCharsets.UTF_8));
										/* 调用下一个处理器 传递消息 （下一个使用独立的group不能在当前线程直接调用 内部会切换线程） */
										ctx.fireChannelRead(msg);
									}
								}).
								addLast(group, "handler02", new ChannelInboundHandlerAdapter() {
									/* 使用于执行处理器方法的EventLoopGroup(用group的线程)、处理器的名字、处理器 */
									@Override
									public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
										ByteBuf buf = (ByteBuf) msg;
										LOGGER.debug(buf.toString(StandardCharsets.UTF_8));
									}
								});
					}
				})
				.bind(8080);
	}
}
