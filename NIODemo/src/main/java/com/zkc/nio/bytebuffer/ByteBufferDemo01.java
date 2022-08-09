package com.zkc.nio.bytebuffer;

import java.io.FileInputStream;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class ByteBufferDemo01 {
	
	public static void main(String[] args) {
		//获取FileChannel 1、I/O流 2、RandomAccessFile
		try {
			URL url = ByteBufferDemo01.class.getClassLoader().getResource("data.txt");
			if (url == null || url.getPath().length() == 0) {
				System.err.println("文件路径不存在");
				return;
			}
			FileChannel channel = new FileInputStream(url.getPath()).getChannel();
			//创建缓冲区
			ByteBuffer buffer = ByteBuffer.allocate(10);
			//从channel读取数据 向buffer写入
			channel.read(buffer);
			//打印buffer内容 buffer切换至读模式
			buffer.flip();
			while (buffer.hasRemaining()) {
				byte b = buffer.get();
				System.out.println((char) b);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
