package com.zkc.bufferDemo;

import com.zkc.util.Logger;

import java.nio.IntBuffer;

/**
 * Buffer类的几个常用方法，包含Buffer实例的创
 * 建、写入、读取、重复读、标记和重置等。
 */
public class UserBuffer {
	
	public static void main(String[] args) {
		Logger.debug("分配内存");
		allocateTest();
	}
	
	private static IntBuffer intBuffer;
	
	/**
	 * 获取一个Buffer实例对象时，并不是使
	 * 用子类的构造器来创建，而是调用子类的allocate()方法
	 */
	private static void allocateTest() {
		intBuffer = IntBuffer.allocate(20);
		Logger.debug("------------after allocate------------------");
		Logger.debug("position=" + intBuffer.position());
		Logger.debug("limit=" + intBuffer.limit());
		Logger.debug("capacity=" + intBuffer.capacity());
	}
}
