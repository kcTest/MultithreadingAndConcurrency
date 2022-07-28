package com.zkc.channelDemo.socketChannel;

import com.zkc.NIODemoConfig;
import com.zkc.util.IOUtil;
import com.zkc.util.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * Java NIO中所有网络连接socket通道都继承了SelectableChannel类，都是可选择的。
 * socket通道可以设置为非阻塞模式。
 * socket通道继承SelectableChannel，因此是可选择通道。
 * socket通道能与选择器一起使用。
 */
public class NIOSendClient {
	
	public static void main(String[] args) {
		//启动客户端连接
		NIOSendClient client = new NIOSendClient();
		//传输文件
		client.sendFile();
	}
	
	private Charset charset = StandardCharsets.UTF_8;
	
	/**
	 * 向服务端传输文件
	 */
	private void sendFile() {
		try {
			String srcFileName = NIODemoConfig.SRC_FILENAME_SOCKET_SEND;
			String srcPath = IOUtil.getResourcePath(srcFileName);
			Logger.debug("srcPath=" + srcPath);
			File srcFile = new File(srcPath);
			if (!srcFile.exists()) {
				Logger.debug("srcFile 不存在");
				return;
			}
			//创建连接
			SocketChannel socketChannel = SocketChannel.open();
			socketChannel.socket().connect(new InetSocketAddress(NIODemoConfig.SOCKET_SERVER_IP, NIODemoConfig.SOCKET_SERVER_PORT));
			socketChannel.configureBlocking(false);
			while (!socketChannel.finishConnect()) {
				Logger.debug("正在连接服务器...");
			}
			Logger.debug("连接服务器成功");
			
			//发送文件名称长度、文件名称、文件长度、文件内容
			
			//发送的文件名称
			String destFileName = NIODemoConfig.DEST_FILENAME_SOCKET_RECEIVE;
			//待读取的存放文件名称的缓冲
			ByteBuffer srcFileNameBuf = charset.encode(destFileName);
			//待发送文件名称的长度
			int fileNameLen = srcFileNameBuf.limit();
			//用于存放文件名称长度、文件长度的缓冲、文件内容的缓冲 待写入
			ByteBuffer srcBuf = ByteBuffer.allocate(NIODemoConfig.SEND_BUFFER_SIZE);
			
			//先写入发送文件名称长度
			srcBuf.putInt(fileNameLen);
			srcBuf.flip();
			socketChannel.write(srcBuf);
			srcBuf.clear();
			Logger.info("文件名称长度发送完成", fileNameLen);
			//再发送文件名称
			socketChannel.write(srcFileNameBuf);
			Logger.info("文件名称发送完成", destFileName);
			//再发送文件长度
			long fileLen = srcFile.length();
			srcBuf.putLong(fileLen);
			srcBuf.flip();
			socketChannel.write(srcBuf);
			//再次清空准备用来发送文件内容
			srcBuf.clear();
			Logger.info("文件长度发送完成", fileLen);
			//再发送文件内容
			Logger.debug("开始传输文件");
			FileChannel ic = new FileInputStream(srcFile).getChannel();
			long total = ic.size(), pos = 0;
			while (pos < total) {
				pos += ic.read(srcBuf);
				srcBuf.flip();
				socketChannel.write(srcBuf);
				srcBuf.clear();
				Logger.debug("|" + (pos / total) * 100 + "% |");
			}
			if (pos == total) {
				IOUtil.closeQuietly(ic);
				socketChannel.shutdownInput();
				IOUtil.closeQuietly(socketChannel);
			}
			Logger.debug("===============文件传输完成==================");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
}
