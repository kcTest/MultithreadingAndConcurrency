package com.zkc.reactor;

import com.zkc.NIODemoConfig;
import com.zkc.util.DateUtil;
import com.zkc.util.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.Set;

public class EchoClient {
	public static void main(String[] args) throws IOException {
		new EchoClient().start();
	}
	
	private void start() throws IOException {
		SocketChannel socketChannel = SocketChannel.open(new InetSocketAddress(NIODemoConfig.SOCKET_SERVER_IP,
				NIODemoConfig.SOCKET_SERVER_PORT));
		socketChannel.configureBlocking(false);
		while (!socketChannel.finishConnect()) {
		}
		Logger.info("连接服务器成功");
		//使用新线程注册并处理读写事件
		Processor processor = new Processor(socketChannel);
		new Thread(processor).start();
	}
	
	private static class Processor implements Runnable {
		
		private final Selector selector;
		
		public Processor(SocketChannel socketChannel) throws IOException {
			this.selector = Selector.open();
			//注册读写事件
			socketChannel.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);
		}
		
		@Override
		public void run() {
			try {
				while (!Thread.interrupted()) {
					selector.select();
					Set<SelectionKey> keys = selector.selectedKeys();
					for (SelectionKey key : keys) {
						//输入并发送到服务端
						if (key.isWritable()) {
							ByteBuffer buf = ByteBuffer.allocate(NIODemoConfig.SEND_BUFFER_SIZE);
							Scanner scanner = new Scanner(System.in);
							Logger.info("请输入发送内容");
							if (scanner.hasNext()) {
								SocketChannel channel = (SocketChannel) key.channel();
								String msg = DateUtil.getNow() + " >>" + scanner.next();
								buf.put(msg.getBytes(StandardCharsets.UTF_8));
								//先写再读
								buf.flip();
								channel.write(buf);
								buf.clear();
							}
						} else if (key.isReadable()) {
							SocketChannel channel = (SocketChannel) key.channel();
							//接收服务器发送回来的数据
							ByteBuffer buf = ByteBuffer.allocate(NIODemoConfig.SEND_BUFFER_SIZE);
							int readLen;
							while ((readLen = channel.read(buf)) > 0) {
								buf.flip();
								Logger.info("server echo:" + new String(buf.array(), 0, readLen));
								buf.clear();
							}
						}
					}
					keys.clear();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
