package com.zkc.nio.bytebuffer;

import java.nio.ByteBuffer;

import static com.zkc.nio.util.ByteBufferUtil.debugAll;

public class ByteBufferDemo03 {
	
	public static void main(String[] args) {
		//创建缓冲区
		ByteBuffer buffer = ByteBuffer.allocate(10);
		buffer.put((byte) 0x61);
		debugAll(buffer);
		buffer.put(new byte[]{(byte) 0x62, (byte) 0x63, (byte) 0x64});
		debugAll(buffer);
		
		buffer.flip();
		System.out.println((char) buffer.get());
		
		buffer.compact();
		debugAll(buffer);
		
		//从未读完的数据之后开始写
		buffer.put((byte) 0x65);
		debugAll(buffer);
		
	}
}
