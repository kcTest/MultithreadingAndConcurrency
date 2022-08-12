package com.zkc.nio.selector.singleThread;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * 阻塞模式 单线程
 * <p>
 * 接收数据
 */
public class ClientDemo67 {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ClientDemo67.class);
	
	public static void main(String[] args) throws IOException {
		SocketChannel sc = SocketChannel.open();
		sc.connect(new InetSocketAddress(8080));
		
		int read = 0;
		while (true) {
			ByteBuffer buffer = ByteBuffer.allocate(1024 * 1024);
			read += sc.read(buffer);
			LOGGER.info("read: " + read);
			buffer.clear();
		}
	}
}
