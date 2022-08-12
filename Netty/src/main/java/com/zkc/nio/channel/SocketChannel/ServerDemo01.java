package com.zkc.nio.channel.SocketChannel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;

import static com.zkc.nio.util.ByteBufferUtil.debugAll;

/**
 * 阻塞模式 单线程负责多个客户端连接
 */
public class ServerDemo01 {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ServerDemo01.class);
	
	public static void main(String[] args) throws IOException {
		//数据缓冲区
		ByteBuffer buffer = ByteBuffer.allocate(16);
		//创建服务器
		ServerSocketChannel ssc = ServerSocketChannel.open();
		//监听端口
		ssc.bind(new InetSocketAddress(8080));
		//管理连接
		List<SocketChannel> channels = new ArrayList<>();
		while (true) {
			//建立客户端连接
			LOGGER.info("正在建立连接...");
			//没有连接会阻塞住 影响其它客户端的信息读取
			SocketChannel sc = ssc.accept();
			LOGGER.info("已连接");
			channels.add(sc);
			for (SocketChannel channel : channels) {
				LOGGER.info("读取前 " + channel);
				//读不到数据会阻塞 影响其它客户端的连接
				channel.read(buffer);
				buffer.flip();
				debugAll(buffer);
				buffer.clear();
				LOGGER.info("读取后 " + channel);
			}
		}
		
	}
}
