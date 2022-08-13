package com.zkc.nio.selector.singleThread;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;

/**
 * 非阻塞模式 selector 单线程处理（不能充分利用多核CPU） 多路复用  
 * 消息边界处理:
 * 1.服务端与客户端缓冲区长度固定一致，如果消息数据较小 补齐空余位置容易导致空间浪费
 * 2.通过符号规定消息结束位置 服务端按字节读取判断结束符 按个字节判断效率慢
 * 3.客户端的消息分多个部分，如第一部分用几个字节存放整型数值表示后面要读取的消息长度,第二部分存放消息，只读取指定长度。
 * <p>
 * 附件
 */
public class ServerDemo05 {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ServerDemo05.class);
	
	public static void main(String[] args) throws IOException {
		Selector selector = Selector.open();
		
		ServerSocketChannel ssc = ServerSocketChannel.open();
		ssc.configureBlocking(false);
		ssc.bind(new InetSocketAddress(8080));
		
		SelectionKey serverKey = ssc.register(selector, 0, null);
		serverKey.interestOps(SelectionKey.OP_ACCEPT);
		LOGGER.debug("绑定的key: " + serverKey);
		
		while (true) {
			selector.select();
			Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
			while (iterator.hasNext()) {
				SelectionKey curKey = iterator.next();
				LOGGER.debug("当前处理的key: " + curKey);
				if (curKey.isAcceptable()) {
					ServerSocketChannel serverChannel = (ServerSocketChannel) curKey.channel();
					
					SocketChannel clientChannel = serverChannel.accept();
					clientChannel.configureBlocking(false);
					SelectionKey clientKey = clientChannel.register(selector, 0, null);
					clientKey.interestOps(SelectionKey.OP_READ);
					//为clientKey添加附件 让每个channel拥有各自的buffer，buffer不再作为局部变量共享使用
					Buffer buffer = ByteBuffer.allocate(16);
					clientKey.attach(buffer);
					
					LOGGER.debug("当前客户端sc: " + clientChannel);
				} else if (curKey.isReadable()) {
					
					try {
						//通过key中取出channel对应的buffer   
						ByteBuffer buffer = (ByteBuffer) curKey.attachment();
						SocketChannel clientChannel = (SocketChannel) curKey.channel();
						//一次没有读完 会再次触发read事件
						int read = clientChannel.read(buffer);
						if (read == -1) {
							curKey.cancel();
						} else {
							splitMsg(buffer);
							//当前buffer已满 扩容 并复制数据到新的buffer上 再用新buffer作为key的附件
							if (buffer.position() == buffer.limit()) {
								//容量只能增大 复制影响效率
								ByteBuffer buffer2 = ByteBuffer.allocate(buffer.capacity() * 2);
								LOGGER.debug("new capacity: " + buffer2.capacity());
								//复制前 切换原buffer到读模式
								buffer.flip();
								buffer2.put(buffer);
								curKey.attach(buffer2);
							}
						}
					} catch (IOException e) {
						e.printStackTrace();
						curKey.cancel();
					}
				}
				iterator.remove();
			}
		}
		
	}
	
	private static void splitMsg(ByteBuffer from) {
		from.flip();
		int size = from.limit();
		for (int i = 0; i < size; i++) {
			if (from.get(i) == '|') {
				ByteBuffer to = ByteBuffer.allocate(i - from.position());
				while (from.hasRemaining()) {
					byte b = from.get();
					if (b == '|') {
						to.flip();
						LOGGER.debug(StandardCharsets.UTF_8.decode(to).toString());
						break;
					} else {
						to.put(b);
					}
				}
			}
		}
		from.compact();
	}
}
