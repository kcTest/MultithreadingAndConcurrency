package com.zkc.example;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;

@Slf4j
public class NettyServer {
	
	public static void main(String[] args) {
		ServerBootstrap serverBootstrap = new ServerBootstrap();
		
		NioEventLoopGroup boss = new NioEventLoopGroup();
		NioEventLoopGroup worker = new NioEventLoopGroup();
		/*指定线程模型*/
		serverBootstrap.group(boss, worker)
				/*指定线程模型NIO*/
				.channel(NioServerSocketChannel.class)
				/*指定客户端连接处理逻辑*/
				.childHandler(new ChannelInitializer<NioSocketChannel>() {
					@Override
					protected void initChannel(NioSocketChannel ch) throws Exception {
//						log.info("clientKey:{}", ch.attr(AttributeKey.valueOf("clientKey")));
//						ch.pipeline().addLast(new StringDecoder())
//								.addLast(new SimpleChannelInboundHandler<String>() {
//									@Override
//									protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
//										log.info(msg);
//									}
//								});
						ch.pipeline().addLast(new FirstServerHandler());
						
					}
				});

//				/*
//				给服务端Channel设置TCP参数
//				系统用于临时存放已完成三次握手的请求的队列的最大长度，如果连接建立频繁，服务器处理创建新连接较慢，则可以适当调大这个参数
//				*/
//				.option(ChannelOption.SO_BACKLOG, 1024)

//				/*
//		    	给客户端Channel设置TCP参数
//		    	ChannelOption.SO_KEEPALIVE表示是否开启TCP底层心跳机制，true表示开启
//		    	ChannelOption.TCP_NODELAY表示是否开启Nagle算法，true表示关闭，false表示开启。通俗地说，如果要求高实时性，有数据发送时就马上发送，就设置为关闭；如果需要减少发送次数，减少网络交互，就设置为开启
//Nagle's algorithm works by combining a number of small outgoing messages and sending them all at once. Specifically, as long as there is a sent packet for which the sender has received no acknowledgment, the sender should keep buffering its output until it has a full packet's worth of output, thus allowing output to be sent all at once.
//		    	*/
//				.childOption(ChannelOption.SO_KEEPALIVE, true)
//				.childOption(ChannelOption.TCP_NODELAY, true)
//		        /*发送缓冲区和接收缓冲区大小*/
//				.childOption(ChannelOption.SO_SNDBUF, true)
//				.childOption(ChannelOption.SO_RCVBUF, true)
		
//				/*为客户端Channel维护一个存放自定义数据的Map*/
//				.childAttr(AttributeKey.newInstance("clientKey"), "value");

//				/*指定在服务端启动过程中的一些逻辑*/
//				.handler()

//				/*为服务端Channel维护一个存放自定义数据的Map*/
//				.attr(AttributeKey.newInstance("serverKey"), "value");
		
		/*绑定端口启服务器*/
		bind(serverBootstrap, 1000);
	}
	
	/**
	 * 自动绑定递增端口
	 */
	private static void bind(final ServerBootstrap serverBootstrap, final int port) {
		/*绑定端口会在EventLoop的线程执行*/
		serverBootstrap.bind(port).addListener(new GenericFutureListener<Future<? super Void>>() {
			@Override
			public void operationComplete(Future<? super Void> future) throws Exception {
				if (future.isSuccess()) {
					log.info("端口{}绑定成功", port);
				} else {
					log.info("端口{}绑定失败", port);
					serverBootstrap.bind(port + 1);
				}
			}
		});
	}
	
	private static class FirstServerHandler extends ChannelInboundHandlerAdapter {
		@Override
		public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
			ByteBuf buf = (ByteBuf) msg;
			log.info(" 服务端读到数据 -> {}", buf.toString(StandardCharsets.UTF_8));
			
			log.info("服务端写数据");
			ByteBuf bufOut = getByteBuf(ctx);
			ctx.channel().writeAndFlush(bufOut);
		}
		
		private ByteBuf getByteBuf(ChannelHandlerContext ctx) {
			//1、获取二进制抽象 ByteBuf
			ByteBuf buf = ctx.alloc().buffer();
			//2、准备数据
			byte[] bytes = "world".getBytes(StandardCharsets.UTF_8);
			//3、填充数据到ByteBuf
			buf.writeBytes(bytes);
			
			return buf;
		}
		
	}
	
}
