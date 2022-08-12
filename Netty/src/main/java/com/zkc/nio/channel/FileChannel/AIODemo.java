package com.zkc.nio.channel.FileChannel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.CompletionHandler;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import static com.zkc.nio.util.ByteBufferUtil.debugAll;

/**
 * 异步IO
 */
public class AIODemo {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(AIODemo.class);
	
	public static void main(String[] args) throws InterruptedException, IOException {
		URL url = AIODemo.class.getClassLoader().getResource("data01.txt");
		if (url == null || url.getPath().length() == 0) {
			LOGGER.error("资源文件不存在");
			return;
		}
		try {
			AsynchronousFileChannel channel = AsynchronousFileChannel.open(Paths.get(url.toURI()), StandardOpenOption.READ);
			ByteBuffer buffer = ByteBuffer.allocate(20);
			LOGGER.debug("start read");
			channel.read(buffer, 0, buffer, new CompletionHandler<Integer, ByteBuffer>() {
				@Override
				public void completed(Integer result, ByteBuffer attachment) {
					LOGGER.debug("read completed: " + result);
					attachment.flip();
					debugAll(attachment);
				}
				
				@Override
				public void failed(Throwable exc, ByteBuffer attachment) {
					LOGGER.error("read failed");
				}
			});
			LOGGER.debug("end read");
		} catch (Exception e) {
			e.printStackTrace();
		}
		/**
		 * 让守护线程执行完 回调方法
		 */
		System.in.read();
	}
}
