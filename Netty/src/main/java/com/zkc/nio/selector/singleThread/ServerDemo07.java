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
 * 向客户端的发送大量数据 分多次写
 */
public class ServerDemo07 {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ServerDemo07.class);
	
	public static void main(String[] args) throws IOException {
		Selector selector = Selector.open();
		
		ServerSocketChannel ssc = ServerSocketChannel.open();
		ssc.configureBlocking(false);
		ssc.bind(new InetSocketAddress(8080));
		ssc.register(selector, SelectionKey.OP_ACCEPT);
		
		while (true) {
			//wakeup唤醒、 选择器close、线程interrupt
			selector.select();
			Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
			while (iterator.hasNext()) {
				SelectionKey curKey = iterator.next();
				if (curKey.isAcceptable()) {
					LOGGER.debug("isAcceptable");
					SocketChannel clientChannel = ssc.accept();
					clientChannel.configureBlocking(false);
					SelectionKey clientKey = clientChannel.register(selector, 0, null);
					
					StringBuilder sb = new StringBuilder();
					for (int i = 0; i < 3000000; i++) {
						sb.append('a');
					}
					ByteBuffer buffer = StandardCharsets.UTF_8.encode(sb.toString());
					int write = clientChannel.write(buffer);
					LOGGER.debug("write len:" + write);
					//  先发送  不循环等待是否可写 检查是否还有未发送的数据 
					if (buffer.hasRemaining()) {
						//一次没有写完 设置关注可写事件（与原有事件合并）之后 会再次触发可写事件 再继续写  
						clientKey.interestOps(SelectionKey.OP_WRITE | clientKey.interestOps());
						//将未写完的数据作为key的附件 下次可写时取出
						clientKey.attach(buffer);
					}
				} else if (curKey.isWritable()) {
					LOGGER.debug("isWritable");
					//取出未发送完的数据 继续写
					ByteBuffer buffer = (ByteBuffer) curKey.attachment();
					SocketChannel clientChannel = (SocketChannel) curKey.channel();
					int write = clientChannel.write(buffer);
					LOGGER.debug("write len:" + write);
					//清理buffer附件
					if (!buffer.hasRemaining()) {
						curKey.attach(null);
						//移除关注的可写事件
						curKey.interestOps(curKey.interestOps() ^ SelectionKey.OP_WRITE);
					}
				}
				iterator.remove();
			}
		}
		
	}
	
}
