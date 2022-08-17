package com.zkc.chat.client;

import com.zkc.chat.client.console.ConsoleCommandManager;
import com.zkc.chat.client.console.LoginConsoleCommand;
import com.zkc.chat.client.handler.*;
import com.zkc.chat.codec.PacketCodecHandler;
import com.zkc.chat.codec.Spliter;
import com.zkc.chat.handler.IMIdleStateHandler;
import com.zkc.chat.util.SessionUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.extern.slf4j.Slf4j;

import java.util.Scanner;
import java.util.concurrent.TimeUnit;

@Slf4j
public class NettyClient {
	
	private static final int MAX_RETRY = 5;
	private static final String HOST = "localhost";
	private static final int PORT = 8000;
	
	public static void main(String[] args) {
		NioEventLoopGroup workerGroup = new NioEventLoopGroup();
		
		final Bootstrap bootstrap = new Bootstrap();
		bootstrap.group(workerGroup)
				.channel(NioSocketChannel.class)
				.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
				.option(ChannelOption.SO_KEEPALIVE, true)
				.option(ChannelOption.TCP_NODELAY, true)
				.handler(new ChannelInitializer<NioSocketChannel>() {
					@Override
					protected void initChannel(NioSocketChannel ch) throws Exception {
						//空闲检测
						ch.pipeline()
								.addLast(new IMIdleStateHandler())
								.addLast(new Spliter())
								.addLast(PacketCodecHandler.INSTANCE)
								//处理登录响应
								.addLast(new LoginResponseHandler())
								//收消息处理器
								.addLast(new MessageResponseHandler())
								//创建群响应处理器
								.addLast(new JoinGroupResponseHandler())
								//退群响应处理器你
								.addLast(new QuitGroupResponseHandler())
								//获取群成员响应处理器
								.addLast(new ListGroupMembersResponseHandler())
								//群消息响应处理器
								.addLast(new GroupMessageResponseHandler())
								//登出响应处理器你
								.addLast(new LogoutResponseHandler())
								//心跳定时器
								.addLast(new HeartBeatTimerHandler());
					}
				});
		
		connect(bootstrap, HOST, PORT, 1);
	}
	
	/**
	 * 　失败重连
	 */
	private static void connect(Bootstrap bootstrap, String host, int port, int retry) {
		bootstrap.connect(host, port).addListener(new GenericFutureListener<ChannelFuture>() {
			@Override
			public void operationComplete(ChannelFuture future) throws Exception {
				if (future.isSuccess()) {
					log.info("连接成功, 启动控制台线程......");
					Channel channel = future.channel();
					startConsoleThread(channel);
				} else {
					if (retry == MAX_RETRY) {
						log.error("重试次数已用完，放弃连接");
						return;
					}
					log.error("第{}次重连失败...进行第{}次重连...", retry, retry + 1);
					int delay = 1 << (retry - 1);
					bootstrap.config().group().schedule(() -> {
						connect(bootstrap, host, port, retry + 1);
					}, delay, TimeUnit.SECONDS);
				}
			}
		});
	}
	
	private static void startConsoleThread(Channel channel) {
		ConsoleCommandManager consoleCommandManager = new ConsoleCommandManager();
		LoginConsoleCommand loginConsoleCommand = new LoginConsoleCommand();
		Scanner scanner = new Scanner(System.in);
		
		new Thread(() -> {
			while (!Thread.interrupted()) {
				if (!SessionUtil.hasLogin(channel)) {
					loginConsoleCommand.exec(scanner, channel);
				} else {
					consoleCommandManager.exec(scanner, channel);
				}
			}
		}).start();
	}
	
}
