package com.zkc.designPattern.singleton;

/**
 * 使用内置锁保护懒汉式单例
 * <p>
 * 在争用激烈的场景下，内置锁会升级为重量级锁，开销大、性能差
 */
public class SingletonDemo03 {
	
	/**
	 * 静态成员
	 */
	private static SingletonDemo03 instance;
	
	/**
	 * 私有构造器
	 */
	private SingletonDemo03() {
	}
	
	public static synchronized SingletonDemo03 getInstance() {
		if (instance == null) {
			instance = new SingletonDemo03();
		}
		return instance;
	}
	
}
