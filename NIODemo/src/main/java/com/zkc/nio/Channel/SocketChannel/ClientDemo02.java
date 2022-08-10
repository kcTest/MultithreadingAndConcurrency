package com.zkc.nio.Channel.SocketChannel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

/**
 * 非阻塞模式
 */
public class ClientDemo02 {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ClientDemo02.class);
	
	public static void main(String[] args) throws IOException {
		ByteBuffer buffer = ByteBuffer.allocate(32);
		
		SocketChannel sc = SocketChannel.open();
		sc.connect(new InetSocketAddress(8080));
		
		
		LOGGER.info("输入...");
		Scanner scanner = new Scanner(System.in);
		while (scanner.hasNext()) {
			String s = scanner.nextLine();
			buffer.put(StandardCharsets.UTF_8.encode(s));
			buffer.flip();
			sc.write(buffer);
			buffer.clear();
		}
		LOGGER.info("结束...");
	}
}
