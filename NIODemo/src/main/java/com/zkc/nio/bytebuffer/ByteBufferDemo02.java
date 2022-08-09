package com.zkc.nio.bytebuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class ByteBufferDemo02 {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ByteBufferDemo02.class);
	
	public static void main(String[] args) {
		//获取FileChannel 1、I/O流 2、RandomAccessFile
		try {
			URL url = ByteBufferDemo02.class.getClassLoader().getResource("data.txt");
			if (url == null || url.getPath().length() == 0) {
				LOGGER.error("文件路径不存在");
				return;
			}
			FileChannel channel = new FileInputStream(url.getPath()).getChannel();
			//创建缓冲区
			ByteBuffer buffer = ByteBuffer.allocate(10);
			//从channel读取数据 向buffer写入
			while (true) {
				int len = channel.read(buffer);
				LOGGER.debug("读取字节长度: " + len);
				if (len == -1) {
					break;
				}
				//打印buffer内容 buffer切换至读模式
				buffer.flip();
				while (buffer.hasRemaining()) {
					byte b = buffer.get();
					LOGGER.debug("当前字节: " + (char) b);
				}
				//buffer切换成写模式
				buffer.clear();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
