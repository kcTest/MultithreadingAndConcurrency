package com.zkc.chat;

/**
 * 序列化接口 Java对象转换成二进制数据的规则
 */
public interface Serializer {
	/**
	 * 序列化算法的类型
	 */
	byte JSON_SERIALIZER = 1;
	
	/**
	 * 默认序列化算法
	 */
	Serializer DEFAULT = new JsonSerializer();
	
	/**
	 * 获取具体的序列化算法标识
	 */
	byte getSerializerAlgorithm();
	
	/**
	 * Java对象转换成二进制数据
	 */
	byte[] serializer(Object object);
	
	/**
	 * 二进制数据转换成Java对象
	 */
	<T> T deserializer(byte[] bytes, Class<T> clazz);
}
