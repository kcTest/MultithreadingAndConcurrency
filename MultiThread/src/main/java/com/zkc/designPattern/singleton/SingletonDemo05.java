package com.zkc.designPattern.singleton;

/**
 * 双重检查锁单例模式
 */
public class SingletonDemo05 {
	
	/**
	 * 静态成员  保持内存可见性
	 */
	private static volatile SingletonDemo05 instance;
	
	/**
	 * 私有构造器
	 */
	private SingletonDemo05() {
	}
	
	public static SingletonDemo05 getInstance() {
		//检查1
		if (instance == null) {
			//加锁
			synchronized (SingletonDemo05.class) {
				//检查2
				if (instance == null) {
					instance = new SingletonDemo05();
				}
			}
		}
		return instance;
	}
	
}
