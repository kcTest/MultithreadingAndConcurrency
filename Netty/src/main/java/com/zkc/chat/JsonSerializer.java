package com.zkc.chat;

import com.alibaba.fastjson.JSON;

/**
 * 使用gson
 */
public class JsonSerializer implements Serializer {
	@Override
	public byte getSerializerAlgorithm() {
		return SerializerAlgorithm.JSON;
	}
	
	@Override
	public byte[] serializer(Object object) {
		return JSON.toJSONBytes(object);
	}
	
	@Override
	public <T> T deserializer(byte[] bytes, Class<T> clazz) {
		return JSON.parseObject(bytes, clazz);
	}
}
