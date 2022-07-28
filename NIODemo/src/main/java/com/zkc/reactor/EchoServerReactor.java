package com.zkc.reactor;

import com.zkc.NIODemoConfig;
import com.zkc.util.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * EchoServer的功能很简单：读取客户端的输入并回显到客户端，所以也叫回显服务器。
 * 基于Reactor模式来实现，设计三个重要的类：
 * （1）设计一个反应器类：EchoServerReactor类。
 * （2）设计两个处理器类：AcceptorHandler新连接处理器、EchoHandler回显处理器
 * <p>
 * 它是一个单线程版本的Reactor模式，Reactor和所有的Handler实例都在同一条线程中执行
 */
public class EchoServerReactor implements Runnable {
	
	public static void main(String[] args) throws IOException {
		new Thread(new EchoServerReactor()).start();
	}
	
	private Selector selector;
	private ServerSocketChannel serverSocketChannel;
	
	public EchoServerReactor() throws IOException {
		//初始化
		selector = Selector.open();
		//服务端socket通道
		serverSocketChannel = ServerSocketChannel.open();
		InetSocketAddress address = new InetSocketAddress(NIODemoConfig.SOCKET_SERVER_PORT);
		serverSocketChannel.bind(address);
		Logger.info("服务开始监听：" + address);
		serverSocketChannel.configureBlocking(false);
		
		//通道及事件注册到选择器
		SelectionKey key = serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
		//将负责处理接收事件的处理器附加到键
		key.attach(new AcceptorHandler());
	}
	
	@Override
	public void run() {
		try {
			while (!Thread.interrupted() && selector.select() > 0) {
				Set<SelectionKey> keys = selector.selectedKeys();
				for (SelectionKey key : keys) {
					//分发
					dispatch(key);
				}
				//已全部处理
				keys.clear();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void dispatch(SelectionKey key) {
		//使用该键之前附加的处理器 初始有AcceptorHandler 后续会有EchoHandler
		Runnable handler = (Runnable) key.attachment();
		if (handler != null) {
			handler.run();
		}
	}
	
	/**
	 * 新连接处理器
	 */
	private class AcceptorHandler implements Runnable {
		@Override
		public void run() {
			try {
				//对应新连接的socket通道
				SocketChannel channel = serverSocketChannel.accept();
				Logger.info("接收到一个连接");
				if (channel != null) {
					//处理数据回显
					new EchoHandler(selector, channel);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
