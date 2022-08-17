package com.zkc.chat.serializer.impl;

import com.alibaba.fastjson.JSON;
import com.zkc.chat.serializer.Serializer;
import com.zkc.chat.serializer.SerializerAlgorithm;

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
