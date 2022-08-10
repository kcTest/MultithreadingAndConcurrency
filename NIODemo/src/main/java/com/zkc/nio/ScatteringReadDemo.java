package com.zkc.nio;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;

/**
 * 集中写入
 */
public class ScatteringReadDemo {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ScatteringReadDemo.class);
	
	public static void main(String[] args) {
		ByteBuffer buffer1 = StandardCharsets.UTF_8.encode("one");
		ByteBuffer buffer2 = StandardCharsets.UTF_8.encode("two");
		ByteBuffer buffer3 = StandardCharsets.UTF_8.encode("三");
		
		URL url = GatheringWriteDemo.class.getClassLoader().getResource("");
		if (url == null) {
			LOGGER.error("资源目录不存在");
			return;
		}
		
		try {
			String filePath = url.getPath() + "data03.txt";
			RandomAccessFile rw = new RandomAccessFile(filePath, "rw");
			FileChannel rwChannel = rw.getChannel();
			rwChannel.write(new ByteBuffer[]{buffer1, buffer2, buffer3});
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
}
