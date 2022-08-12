package com.zkc.nio.channel.SocketChannel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

/**
 * 阻塞模式 单线程
 */
public class ClientDemo123458 {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ClientDemo123458.class);
	
	public static void main(String[] args) throws IOException {
		//数据缓冲区
		ByteBuffer buffer = ByteBuffer.allocate(16);
		//创建客户端  
		SocketChannel sc = SocketChannel.open();
		//监听端口
		sc.connect(new InetSocketAddress(8080));
		LOGGER.info("输入...");
		Scanner scanner = new Scanner(System.in);
		while (scanner.hasNext()) {
			String s = scanner.nextLine();
			if ("exit".equals(s)) {
				break;
			}
			buffer.put(StandardCharsets.UTF_8.encode(s));
			buffer.flip();
			sc.write(buffer);
			buffer.clear();
		}
		sc.close();
		LOGGER.info("结束...");
	}
}
