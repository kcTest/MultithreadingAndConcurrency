package com.zkc.nio.bytebuffer;

import java.nio.ByteBuffer;

import static com.zkc.nio.util.ByteBufferUtil.debugAll;

public class ByteBufferDemo05 {
	
	public static void main(String[] args) {
		ByteBuffer buffer = ByteBuffer.allocate(10);
		buffer.put(new byte[]{'a', 'b', 'c', 'd'});
		buffer.flip();
		
		buffer.get(new byte[4]);
		debugAll(buffer);
		
		//rewind 从头开始
		buffer.rewind();
		System.out.println((char) buffer.get());
		
		/* mark: 记录position位置 	记录b位置		 */
		System.out.println("记录b位置");
		buffer.mark();

		/*
		mark: 记录position位置 
		resetL：将position重置到mark位置
		 */
		System.out.println((char) buffer.get());
		System.out.println((char) buffer.get());
		System.out.println((char) buffer.get());
		
		System.out.println("重置position到b位置");
		buffer.reset();
		
		System.out.println((char) buffer.get());
		System.out.println((char) buffer.get());
		System.out.println((char) buffer.get());
		
		System.out.println("读取0位置数据,不改变position");
		System.out.println((char) buffer.get(0));
		debugAll(buffer);
	}
}
