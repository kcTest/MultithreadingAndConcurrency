package com.zkc.reactor;

import com.zkc.NIODemoConfig;
import com.zkc.util.Logger;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Channel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

/**
 * 第二个处理器为EchoHandler回显处理器，也是一个传输处理器，主要是完成客户端的内容读取和回显
 */
public class EchoHandler implements Runnable {
	
	private final SelectionKey key;
	private final SocketChannel socketChannel;
	private static final int RECEIVING = 0, SENDING = 1;
	private int state = RECEIVING;
	private final ByteBuffer buf = ByteBuffer.allocate(NIODemoConfig.SEND_BUFFER_SIZE);
	
	public EchoHandler(Selector selector, SocketChannel channel) throws IOException {
		//对应新连接的socket通道
		socketChannel = channel;
		socketChannel.configureBlocking(false);
		//新连接的socket通道注册读事件到选择器 处理器自身再附加到返回的键
		key = socketChannel.register(selector, SelectionKey.OP_READ);
		key.attach(this);
		selector.wakeup();
	}
	
	@Override
	public void run() {
		try {
			if (state == RECEIVING) {
				//将通道中的数据读出客户端发送过来的数据写入到缓冲区
				int readLen;
				while ((readLen = socketChannel.read(buf)) > 0) {
					Logger.info(new String(buf.array(), 0, readLen));
				}
				//先切换模式 下次用于回显到客户端
				buf.flip();
				//读完后注册写事件
				key.interestOps(SelectionKey.OP_WRITE);
				//切换自定义状态 准备发送
				state = SENDING;
			} else if (state == SENDING) {
				//写入通道 回显到客户端
				socketChannel.write(buf);
				buf.clear();
				//注册读事件 准备再次接收数据并读取
				key.interestOps(SelectionKey.OP_READ);
				state = RECEIVING;
			}
		} catch (Exception e) {
			e.printStackTrace();
			key.cancel();
			try {
				socketChannel.finishConnect();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}
}
