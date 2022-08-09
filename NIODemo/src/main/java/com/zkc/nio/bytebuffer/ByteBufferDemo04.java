package com.zkc.nio.bytebuffer;

import java.nio.ByteBuffer;

public class ByteBufferDemo04 {
	
	public static void main(String[] args) {
		/*
		class java.nio.HeapByteBuffer
		 堆内存 读写效率低  受GC影响
		 */
		System.out.println(ByteBuffer.allocate(16).getClass());
		/*
		class java.nio.DirectByteBuffer 
		直接内存  读写效率高	少一次数据拷贝   不受GC影响 不会被复制搬迁 分配内存时效率低 
		使用不当造成内存泄漏
		 */
		System.out.println(ByteBuffer.allocateDirect(16).getClass());
	}
}
