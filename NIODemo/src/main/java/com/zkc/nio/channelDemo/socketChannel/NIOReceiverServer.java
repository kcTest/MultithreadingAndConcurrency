package com.zkc.nio.channelDemo.socketChannel;

import com.zkc.NIODemoConfig;
import com.zkc.util.IOUtil;
import com.zkc.util.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * 使用SocketChannel在服务端接收文件
 */
public class NIOReceiverServer {
	
	public static void main(String[] args) {
		NIOReceiverServer server = new NIOReceiverServer();
		server.startServer();
	}
	
	private Map<SelectableChannel, Client> clientMap = new HashMap<>();
	private ByteBuffer buf = ByteBuffer.allocate(NIODemoConfig.SEND_BUFFER_SIZE);
	
	private void startServer() {
		try {
			Selector selector = Selector.open();
			
			ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
			serverSocketChannel.configureBlocking(false);
			serverSocketChannel.socket().bind(new InetSocketAddress(NIODemoConfig.SOCKET_SERVER_PORT));
			serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
			
			Logger.info("正在监听...");
			while (selector.select() > 0) {
				Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
				while (iterator.hasNext()) {
					SelectionKey key = iterator.next();
					if (key.isAcceptable()) {
						SocketChannel socketChannel = serverSocketChannel.accept();
						socketChannel.configureBlocking(false);
						socketChannel.register(selector, SelectionKey.OP_READ);
						//map记录通道及对应客户端
						clientMap.put(socketChannel, new Client((InetSocketAddress) socketChannel.getRemoteAddress()));
						Logger.debug("客户端连入成功");
					} else if (key.isReadable()) {
						process(key);
					}
					iterator.remove();
				}
			}
			serverSocketChannel.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	private void process(SelectionKey key) {
		Client client = clientMap.get(key.channel());
		SocketChannel socketChannel = (SocketChannel) key.channel();
		int readLen;
		try {
			buf.clear();
			while ((readLen = socketChannel.read(buf)) > 0) {
				//准备读出
				buf.flip();
				if (client.fileName == null) {
					//处理文件名长度
					//读取4字节 pos移动4位
					if (buf.limit() < 4) {
						continue;
					}
					int fileNameLen = buf.getInt();
					Logger.info("接收文件名称长度: ", fileNameLen);
					
					//处理文件名
					byte[] fileNameBytes = new byte[fileNameLen];
					//读取长度为文件名称长度 正好读出文件名
					buf.get(fileNameBytes);
					String destFileName = new String(fileNameBytes, StandardCharsets.UTF_8);
					client.fileName = destFileName;
					Logger.info("接收文件名称: ", destFileName);
					File destDirectory = new File(NIODemoConfig.DEST_DIRECTORY_SOCKET_RECEIVE);
					if (!destDirectory.exists()) {
						destDirectory.mkdir();
					}
					String fileFullPath = destDirectory.getAbsolutePath() + File.separator + destFileName;
					Logger.info("文件完整保存路径: ", fileFullPath);
					//创建文件及提前设置通道
					File destFile = new File(fileFullPath);
					if (!destFile.exists()) {
						destFile.createNewFile();
					}
					client.outChannel = new FileOutputStream(destFile).getChannel();
					
					//处理文件内容长度
					if (buf.limit() < 8) {
						continue;
					}
					client.fileLength = buf.getLong();
					Logger.info("接收文件内容长度: ", client.fileLength);
					client.startTime = System.currentTimeMillis();
					Logger.debug("开始传输...");
					
					client.receiveLength += buf.capacity();
					if (buf.limit() > 0) {
						// 写入文件
						client.outChannel.write(buf);
					}
				} else {
					client.receiveLength += buf.limit();
					client.outChannel.write(buf);
				}
				if (client.isFinished()) {
					break;
				}
				buf.clear();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return;
		} finally {
			key.cancel();
		}
		if (readLen == -1) {
			finish(key, client);
		}
	}
	
	private void finish(SelectionKey key, Client client) {
		IOUtil.closeQuietly(client.outChannel);
		Logger.info("传输完成");
		key.cancel();
		Logger.info("文件接收成功,文件名" + client.fileName, "大小:", client.fileLength);
		Logger.info("传输时间:", System.currentTimeMillis() - client.startTime, "s");
	}
	
	/**
	 * 自定义客户端信息
	 */
	private class Client {
		public String fileName;
		public long fileLength;
		public long startTime;
		public InetSocketAddress address;
		public FileChannel outChannel;
		public long receiveLength;
		
		public Client(InetSocketAddress address) {
			this.address = address;
		}
		
		public boolean isFinished() {
			return receiveLength >= fileLength;
		}
	}
}
