package com.zkc.channelDemo.socketChannel;

import com.zkc.NIODemoConfig;
import com.zkc.util.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class NIODiscardClient {
	public static void main(String[] args) {
		startClient();
	}
	
	private static void startClient() {
		try {
			//建立连接
			SocketChannel channel = SocketChannel.open(new InetSocketAddress(NIODemoConfig.SOCKET_SERVER_IP,
					NIODemoConfig.SOCKET_SERVER_PORT));
			channel.configureBlocking(false);
			while (!channel.finishConnect()) {
				Logger.debug("正在连接服务器...");
			}
			Logger.debug("连接服务器成功");
			//发送数据
			ByteBuffer buf = ByteBuffer.allocate(NIODemoConfig.SEND_BUFFER_SIZE);
			buf.put(StandardCharsets.UTF_8.encode("你好"));
			Logger.info("你好");
			buf.flip();
			channel.write(buf);
			channel.shutdownInput();
			channel.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
