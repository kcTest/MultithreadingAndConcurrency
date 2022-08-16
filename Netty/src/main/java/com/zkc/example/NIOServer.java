package com.zkc.example;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Set;

@Slf4j
public class NIOServer {
	
	public static void main(String[] args) throws IOException {
		Selector serverSelector = Selector.open();
		Selector clientSelector = Selector.open();
		
		new Thread(() -> {
			//对应IO编程中的服务端启动
			try {
				ServerSocketChannel listenerChannel = ServerSocketChannel.open();
				listenerChannel.socket().bind(new InetSocketAddress(8000));
				listenerChannel.configureBlocking(false);
				listenerChannel.register(serverSelector, SelectionKey.OP_ACCEPT);
				
				while (true) {
					//检测是否有新连接 阻塞时间1ms
					if (serverSelector.select(1) > 0) {
						Set<SelectionKey> selectionKeys = serverSelector.selectedKeys();
						Iterator<SelectionKey> keyIterator = selectionKeys.iterator();
						while (keyIterator.hasNext()) {
							SelectionKey key = keyIterator.next();
							if (key.isAcceptable()) {
								try {
									//1、每来一个新连接 不需要创建一个线程	 而是直接注册到clientServer
									SocketChannel clientChannel = ((ServerSocketChannel) key.channel()).accept();
									clientChannel.configureBlocking(false);
									clientChannel.register(clientSelector, SelectionKey.OP_READ);
								} finally {
									keyIterator.remove();
								}
							}
						}
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}).start();
		
		new Thread(() -> {
			while (true) {
				//2、批量查询哪些连接有数据可读 阻塞时间为1ms
				try {
					if (clientSelector.select(1) > 0) {
						Set<SelectionKey> selectionKeys = clientSelector.selectedKeys();
						Iterator<SelectionKey> keyIterator = selectionKeys.iterator();
						while (keyIterator.hasNext()) {
							SelectionKey key = keyIterator.next();
							if (key.isReadable()) {
								try {
									SocketChannel clientChannel = (SocketChannel) key.channel();
									ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
									// 3、面向buff
									clientChannel.read(byteBuffer);
									byteBuffer.flip();
									log.debug("{}", Charset.defaultCharset().newDecoder().decode(byteBuffer));
								} finally {
									keyIterator.remove();
									key.interestOps(SelectionKey.OP_READ);
								}
							}
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}
}
