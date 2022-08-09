package com.zkc.nio.bytebuffer;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import static com.zkc.nio.util.ByteBufferUtil.debugAll;

/**
 * 字符串转bytebuffer
 */
public class ByteBufferDemo06 {
	
	public static void main(String[] args) {
		
		ByteBuffer buffer1 = ByteBuffer.allocate(16);
		buffer1.put("str".getBytes());
		debugAll(buffer1);
		buffer1.flip();
		System.out.println(StandardCharsets.UTF_8.decode(buffer1));
		
		ByteBuffer buffer2 = StandardCharsets.UTF_8.encode("str");
		debugAll(buffer2);
		System.out.println(StandardCharsets.UTF_8.decode(buffer2));
		
		ByteBuffer buffer3 = ByteBuffer.wrap("str".getBytes());
		debugAll(buffer3);
		System.out.println(StandardCharsets.UTF_8.decode(buffer3));
	}
}
