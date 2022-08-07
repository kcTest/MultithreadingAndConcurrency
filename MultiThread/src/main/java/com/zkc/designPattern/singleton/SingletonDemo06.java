package com.zkc.designPattern.singleton;

/**
 * 使用静态内部类实现懒汉式单例模式
 * <p>
 * 只有在getInstance()被调用时才去加载内部类并且初始化单例
 */
public class SingletonDemo06 {
	
	/**
	 * 私有构造器
	 */
	private SingletonDemo06() {
	}
	
	public static SingletonDemo06 getInstance() {
		//返回内部类的静态成员
		return InstanceHolder.INSTANCE;
	}
	
	private static final class InstanceHolder {
		/**
		 * 静态成员  通过final保障初始化时的线程安全
		 */
		static final SingletonDemo06 INSTANCE = new SingletonDemo06();
	}
}
