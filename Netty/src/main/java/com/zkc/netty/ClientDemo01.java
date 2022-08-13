package com.zkc.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;

import java.net.InetSocketAddress;
import java.util.Scanner;

public class ClientDemo01 {
	
	public static void main(String[] args) throws InterruptedException {
		//1、创建启动器
		Channel channel = new Bootstrap()
				//2、添加EventLoop
				.group(new NioEventLoopGroup())
				//3、选择客户端的channel实现  NioSocketChannel：基于NIO的SocketChannel的实现
				.channel(NioSocketChannel.class)
				//4、添加事件处理器  ；连接建立后开始初始化
				.handler(new ChannelInitializer<NioSocketChannel>() {
					@Override
					protected void initChannel(NioSocketChannel ch) throws Exception {
						ch.pipeline().addLast(new StringEncoder());
					}
					//5、连接服务器
				}).connect(new InetSocketAddress(8080))
				.sync()
				.channel();
		//向服务器发送数据
		Scanner scanner = new Scanner(System.in);
		while (scanner.hasNext()) {
			String msg = scanner.nextLine();
			channel.writeAndFlush(msg);
		}
	}
	
}
