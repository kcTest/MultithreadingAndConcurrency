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
 * 非阻塞模式 单线程负责多个客户端连接
 */
public class ServerDemo02 {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ServerDemo02.class);
	
	public static void main(String[] args) throws IOException {
		ByteBuffer buffer = ByteBuffer.allocate(32);
		ServerSocketChannel ssc = ServerSocketChannel.open();
		//改为非阻塞模式
		ssc.configureBlocking(false);
		ssc.bind(new InetSocketAddress(8080));
		List<SocketChannel> channels = new ArrayList<>();
		//非阻塞下 无限循环
		while (true) {
			//非阻塞下直接返回当前结果 继续向下执行 没有连接 sc直接返回Null
			SocketChannel sc = ssc.accept();
			if (sc != null) {
				LOGGER.info("已连接 " + sc);
				//客户端连接也改为非阻塞模式
				sc.configureBlocking(false);
				channels.add(sc);
			}
			for (SocketChannel curSc : channels) {
				int read = curSc.read(buffer);
				//非阻塞读取客户端数据时 没有读到数据返回0
				if (read <= 0) {
					continue;
				}
				buffer.flip();
				debugAll(buffer);
				buffer.clear();
				LOGGER.info("读取后 " + curSc);
			}
		}
		
	}
}
