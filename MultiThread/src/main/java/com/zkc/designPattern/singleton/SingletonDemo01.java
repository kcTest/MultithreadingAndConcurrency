package com.zkc.designPattern.singleton;

/**
 * 简单的饿汉式单例
 * <p>
 * 单例对象在类被加载时，实例就直接被初始化了。很多时候，在类被加载时并不需要进行单例初始化，
 * 所以需要对单例的初始化予以延迟，一直到实例使用的时候初始化
 */
public class SingletonDemo01 {
	/**
	 * 静态成员
	 */
	private static SingletonDemo01 instance = new SingletonDemo01();
	
	/**
	 * 私有构造器
	 */
	private SingletonDemo01() {
	}
	
	public static SingletonDemo01 getInstance() {
		return instance;
	}
	
}
