package com.zkc.chat.server;

import com.zkc.chat.codec.PacketCodecHandler;
import com.zkc.chat.codec.Spliter;
import com.zkc.chat.handler.IMIdleStateHandler;
import com.zkc.chat.server.handler.AuthHandler;
import com.zkc.chat.server.handler.HeartBeatRequestHandler;
import com.zkc.chat.server.handler.IMHandler;
import com.zkc.chat.server.handler.LoginRequestHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NettyServer {
	
	private static final int PORT = 8000;
	
	public static void main(String[] args) {
		NioEventLoopGroup bossGroup = new NioEventLoopGroup();
		NioEventLoopGroup workerGroup = new NioEventLoopGroup();
		
		final ServerBootstrap serverBootstrap = new ServerBootstrap();
		serverBootstrap
				.group(bossGroup, workerGroup)
				.channel(NioServerSocketChannel.class)
				.option(ChannelOption.SO_BACKLOG, 1024)
				.childOption(ChannelOption.SO_KEEPALIVE, true)
				.childOption(ChannelOption.TCP_NODELAY, true)
				.childHandler(new ChannelInitializer<NioSocketChannel>() {
					@Override
					protected void initChannel(NioSocketChannel ch) throws Exception {
						ch.pipeline()
								.addLast(new IMIdleStateHandler())
								.addLast(new Spliter())
								.addLast(PacketCodecHandler.INSTANCE)
//								.addLast(new LoggingHandler())
								.addLast(LoginRequestHandler.INSTANCE)
								.addLast(HeartBeatRequestHandler.INSTANCE)
								.addLast(AuthHandler.INSTANCE)
								.addLast(IMHandler.INSTANCE);
					}
				});
		bind(serverBootstrap, PORT);
	}
	
	private static void bind(ServerBootstrap serverBootstrap, int port) {
		serverBootstrap.bind(port).addListener(future -> {
			if (future.isSuccess()) {
				log.info("端口[{}]绑定成功", port);
			} else {
				log.error("端口[{}]绑定失败！", port);
			}
		});
	}
}
