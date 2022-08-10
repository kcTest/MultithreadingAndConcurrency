package com.zkc.nio.Channel.FileChannel;

import com.zkc.nio.ScatteringReadDemo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.FileChannel;

public class FileChannelDemo {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ScatteringReadDemo.class);
	
	public static void main(String[] args) {
		
		URL url = FileChannelDemo.class.getClassLoader().getResource("");
		if (url == null) {
			LOGGER.error("资源目录不存在");
			return;
		}
		
		try {
			//输入
			FileChannel from = new FileInputStream(url.getPath() + "data01.txt").getChannel();
			//输出
			FileChannel to = new FileOutputStream(url.getPath() + "data04.txt").getChannel();
			//利用操作系统底层零拷贝优化
			long size = from.size();
			for (long left = size; left > 0; ) {
				//剩余变化  读取位置随之变化 一次最多传输2G  较大时分多次传输
				long position = size - left;
				LOGGER.debug("position:" + position + " left:" + left);
				long transferred = from.transferTo(size - left, left, to);
				left -= transferred;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
}
