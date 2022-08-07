package com.zkc.designPattern.singleton;

/**
 * 使用内置锁保护懒汉式单例
 * <p>
 * 在使用的时候才对单例进行初始化
 * <p>
 * 并发执行场景存在着单例被多次创建的问题
 */
public class SingletonDemo02 {
	
	/**
	 * 静态成员
	 */
	private static SingletonDemo02 instance;
	
	/**
	 * 私有构造器
	 */
	private SingletonDemo02() {
	}
	
	public static SingletonDemo02 getInstance() {
		/*不同的线程有可能同时进入条件判断*/
		if (instance == null) {
			instance = new SingletonDemo02();
		}
		return instance;
	}
	
}
