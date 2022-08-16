package com.zkc.example;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Slf4j
public class NettyClient {
	
	public static void main(String[] args) throws InterruptedException {
		Bootstrap bootstrap = new Bootstrap();
		
		NioEventLoopGroup group = new NioEventLoopGroup();
		/*指定线程模型*/
		bootstrap.group(group)
				/*IO模型*/
				.channel(NioSocketChannel.class)
				/*业务处理逻辑*/
				.handler(new ChannelInitializer<NioSocketChannel>() {
					@Override
					protected void initChannel(NioSocketChannel ch) throws Exception {
//						ch.pipeline().addLast(new StringEncoder());
						ch.pipeline().addLast(new FirstClientHandler());
					}
				})
				/*为客户端Channel维护一个存放自定义数据的Map*/
				.attr(AttributeKey.newInstance("clientKey"), "value")
				/*		    	
				 给客户端Channel设置TCP参数
				 */
				.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
				.option(ChannelOption.SO_KEEPALIVE, true)
				.option(ChannelOption.TCP_NODELAY, true);
		;
		log.info("before connect");
		connect(bootstrap, "127.0.0.1", 1000, 1);
	}
	
	private static final int MAX_RETRY = 5;
	
	/**
	 * 　失败重连
	 */
	private static void connect(Bootstrap bootstrap, String host, int port, int retry) {
		bootstrap.connect(host, port).addListener(new GenericFutureListener<ChannelFuture>() {
			@Override
			public void operationComplete(ChannelFuture future) throws Exception {
				if (future.isSuccess()) {
					log.info("连接成功");
//					while (true) {
//						future.channel().writeAndFlush(new Date() + ": hello world");
//						Thread.sleep(2000);
//					}
				} else {
					log.error("{} 第{}次重连失败...", new Date(), retry);
					if (retry == MAX_RETRY) {
						log.error("重试次数已用完，放弃连接");
						return;
					}
					//本次重连间隔
					int delay = 1 << (retry - 1);
					bootstrap.config().group().schedule(() -> {
						connect(bootstrap, host, port, retry + 1);
					}, delay, TimeUnit.SECONDS);
				}
			}
		});
	}
	
	/**
	 * 在客户端建立连接成功之后，向服务端写数据
	 */
	private static class FirstClientHandler extends ChannelInboundHandlerAdapter {
		/**
		 * 客户端连接建立成功之后被调用
		 */
		@Override
		public void channelActive(ChannelHandlerContext ctx) throws Exception {
			log.info("客户端写数据");
			//1、获取数据
			ByteBuf buf = getByteBuf(ctx);
			//2、写数据
			ctx.channel().writeAndFlush(buf);
			
			Thread.sleep(2000);
		}
		
		@Override
		public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
			
			ByteBuf buf = (ByteBuf) msg;
			log.info("客户端读到数据 -> {}", ((ByteBuf) msg).toString(StandardCharsets.UTF_8));
		}
		
		private ByteBuf getByteBuf(ChannelHandlerContext ctx) {
			//1、获取二进制抽象 ByteBuf
			ByteBuf buf = ctx.alloc().buffer();
			//2、准备数据
			byte[] bytes = "hello".getBytes(StandardCharsets.UTF_8);
			//3、填充数据到ByteBuf
			buf.writeBytes(bytes);
			
			return buf;
		}
		
	}
}
