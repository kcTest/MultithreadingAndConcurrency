package com.zkc.designPattern.singleton;

/**
 * 使用双重检查锁+volatile
 * <p>
 */
public class SingletonDemo04 {
	
	/**
	 * 静态成员
	 */
	private static SingletonDemo04 instance;
	
	/**
	 * 私有构造器
	 */
	private SingletonDemo04() {
	}
	
	public static SingletonDemo04 getInstance() {
		//检查1  
		if (instance == null) {
			//加锁
			synchronized (SingletonDemo04.class) {
				//检查2
				if (instance == null) {
					/*
（1）分配一块内存M。
（2）在内存M上初始化Singleton对象。
（3）M的地址赋值给instance变量
					 */
					//指令重排之后，instance有地址值，但实例对象还未初始化 其它线程在检查1处判断不为null 返回了还未初始化的instance
					instance = new SingletonDemo04();
				}
			}
		}
		return instance;
	}
	
}
