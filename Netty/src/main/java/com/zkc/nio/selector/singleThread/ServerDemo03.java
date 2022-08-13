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
import java.util.Iterator;

import static com.zkc.nio.util.ByteBufferUtil.debugAll;

/**
 * 非阻塞模式 selector 单线程处理 多路复用
 */
public class ServerDemo03 {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ServerDemo03.class);
	
	public static void main(String[] args) throws IOException {
		ByteBuffer buffer = ByteBuffer.allocate(32);
		//创建 selector 管理多个channel
		Selector selector = Selector.open();
		
		ServerSocketChannel ssc = ServerSocketChannel.open();
		ssc.configureBlocking(false);
		ssc.bind(new InetSocketAddress(8080));
		
		//将selector与channel关联 通过serverKey处理与当前channel相关的事件
		SelectionKey serverKey = ssc.register(selector, 0, null);
		//设置要关注的事件类型   ServerSocketChannel关注是否有连入的请求
		serverKey.interestOps(SelectionKey.OP_ACCEPT);
		LOGGER.debug("绑定的key: " + serverKey);
		
		while (true) {
			//select方法 没有事件发生阻塞 ； 如果有事件需要处理或取消，否则存在未处理select可以恢复运行 继续不停循环 
			selector.select();
			//获取所有可处理的事件集合 
			Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
			//遍历处理当前事件后再删除
			while (iterator.hasNext()) {
				SelectionKey curKey = iterator.next();
				LOGGER.debug("当前处理的key: " + curKey);
				//区分事件类型
				if (curKey.isAcceptable()) {
					//处理客户端连接请求
					
					//获取与key关联的serverSocketChannel
					ServerSocketChannel serverChannel = (ServerSocketChannel) curKey.channel();
					
					//获取当前客户端的连接通道  如果key被重复处理此处没有连接会返回bull
					SocketChannel clientChannel = serverChannel.accept();
					//设置非阻塞模式
					clientChannel.configureBlocking(false);
					//将selector与channel关联 通过clientKey处理与当前channel相关的事件
					SelectionKey clientKey = clientChannel.register(selector, 0, null);
					//设置要关注的事件类型
					clientKey.interestOps(SelectionKey.OP_READ);
					
					LOGGER.debug("当前客户端sc: " + clientChannel);
				} else if (curKey.isReadable()) {
					//读取客户端发送过来的数据  客户端关闭也会触发
					
					//只有与客户端关联的key关注了read事件 此处不用区分是ServerSocketChannel还是SocketChannel
					try {
						SocketChannel clientChannel = (SocketChannel) curKey.channel();
						//异常断开连接 read抛出异常 取消key
						int read = clientChannel.read(buffer);
						//客户端正常断开 read为返回-1 取消key
						if (read == -1) {
							curKey.cancel();
						} else {
							buffer.flip();
							debugAll(buffer);
							buffer.clear();
						}
					} catch (IOException e) {
						e.printStackTrace();
						//取消出现异常的key  会从集合中移除 不会被重复处理
						curKey.cancel();
					}
				}
				//key在处理后不会被移除  需要手动移除防止重复处理引发的错误
				iterator.remove();
			}
		}
		
	}
}
