package com.zkc.nio;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import static com.zkc.nio.util.ByteBufferUtil.debugAll;

/**
 * 分散读出
 */
public class GatheringWriteDemo {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(GatheringWriteDemo.class);
	
	public static void main(String[] args) {
		URL url = GatheringWriteDemo.class.getClassLoader().getResource("data02.txt");
		if (url == null || url.getPath().length() == 0) {
			LOGGER.error("文件不存在");
			return;
		}
		try { 
			RandomAccessFile read = new RandomAccessFile(url.getPath(), "r");
			FileChannel fileChannel = read.getChannel();
			ByteBuffer buffer1 = ByteBuffer.allocate(3);
			ByteBuffer buffer2 = ByteBuffer.allocate(3);
			ByteBuffer buffer3 = ByteBuffer.allocate(5);
			fileChannel.read(new ByteBuffer[]{buffer1, buffer2, buffer3});
			buffer1.flip();
			buffer2.flip();
			buffer3.flip();
			debugAll(buffer1);
			debugAll(buffer2);
			debugAll(buffer3);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
