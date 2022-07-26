package com.zkc.bufferDemo;

import com.zkc.util.Logger;

import java.nio.IntBuffer;

/**
 * Buffer类的几个常用方法，包含Buffer实例的创
 * 建、写入、读取、重复读、标记和重置等。
 */
public class UserBuffer {
	
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
	
	/**
	 * 调用allocate()方法分配内存、返回了实例对象后，缓冲区实
	 * 例对象处于写模式，可以写入对象，如果要把对象写入缓冲区，就需
	 * 要调用put()方法,要求写入的数据类型与缓冲区的类型保持一致。
	 */
	private static void putTest() {
		for (int i = 0; i < 5; i++) {
			intBuffer.put(i);
		}
		
		Logger.debug("------------after putTest------------------");
		Logger.debug("position=" + intBuffer.position());
		Logger.debug("limit=" + intBuffer.limit());
		Logger.debug("capacity=" + intBuffer.capacity());
	}
	
	public static void main(String[] args) {
		Logger.debug("分配内存");
		allocateTest();
		
		Logger.debug("写入");
		putTest();
	}
}
