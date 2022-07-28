package com.zkc.channelDemo.socketChannel;

import com.zkc.NIODemoConfig;
import com.zkc.util.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;

public class NIODiscardServer {
	public static void main(String[] args) {
		startServer();
	}
	
	private static void startServer() {
		//创建选择器
		try {
			Selector selector = Selector.open();
			//创建服务端通道
			ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
			serverSocketChannel.configureBlocking(false);
			serverSocketChannel.bind(new InetSocketAddress(NIODemoConfig.SOCKET_SERVER_PORT));
			//将通道及连接就绪事件事件注册到选择器
			serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
			//轮询选择器是否有就绪的事件
			while (selector.select() > 0) {
				//如果检测到有新连接就绪事件  创建用于该连接的通道并将通道及可读事件注册到选择器
				Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
				while (iterator.hasNext()) {
					SelectionKey key = iterator.next();
					if (key.isAcceptable()) {
						SocketChannel socketChannel = serverSocketChannel.accept();
						socketChannel.configureBlocking(false);
						socketChannel.register(selector, SelectionKey.OP_READ);
					} else if (key.isReadable()) {
						//如果检测到有可读就绪事件 读取数据到缓存
						SocketChannel socketChannel = (SocketChannel) key.channel();
						ByteBuffer buf = ByteBuffer.allocate(NIODemoConfig.SEND_BUFFER_SIZE);
						buf.flip();
						int readLen;
						while ((readLen = socketChannel.read(buf)) != -1) {
							buf.flip();
							Logger.info(new String(buf.array(), 0, readLen));
							buf.clear();
						}
						socketChannel.close();
					}
					//移除 防止重复处理
					iterator.remove();
				}
			}
			serverSocketChannel.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
