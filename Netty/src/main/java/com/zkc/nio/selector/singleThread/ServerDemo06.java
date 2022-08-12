package com.zkc.nio.selector.singleThread;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;

/**
 * 非阻塞模式 selector 单线程处理 多路复用
 * <p>
 * 向客户端的发送大量数据
 */
public class ServerDemo06 {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ServerDemo06.class);
	
	public static void main(String[] args) throws IOException {
		Selector selector = Selector.open();
		
		ServerSocketChannel ssc = ServerSocketChannel.open();
		ssc.configureBlocking(false);
		ssc.bind(new InetSocketAddress(8080));
		ssc.register(selector, SelectionKey.OP_ACCEPT);
		
		while (true) {
			selector.select();
			Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
			while (iterator.hasNext()) {
				SelectionKey curKey = iterator.next();
				if (curKey.isAcceptable()) {
					//key关联的肯定是ssc
					SocketChannel clientChannel = ssc.accept();
					clientChannel.configureBlocking(false);
					
					//向客户端发送大量数据
					StringBuilder sb = new StringBuilder();
					for (int i = 0; i < 10000000; i++) {
						sb.append('a');
					}
					ByteBuffer buffer = StandardCharsets.UTF_8.encode(sb.toString());
					//缓冲区满了之后 一直循环 无法去做别的事情
					while (buffer.hasRemaining()) {
						//获取实际写入字节数
						int write = clientChannel.write(buffer);
						LOGGER.info(String.valueOf(write));
					}
				}
				iterator.remove();
			}
		}
		
	}
	
}
