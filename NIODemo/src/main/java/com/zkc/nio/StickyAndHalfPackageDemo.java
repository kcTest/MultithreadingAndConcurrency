package com.zkc.nio;

import java.nio.ByteBuffer;

import static com.zkc.nio.util.ByteBufferUtil.debugAll;

public class StickyAndHalfPackageDemo {
	public static void main(String[] args) {
		ByteBuffer buffer = ByteBuffer.allocate(32);
		buffer.put("Hello,world\nI'm zhangsan\nHo".getBytes());
		split(buffer);
		buffer.put("w are you?\n".getBytes());
		split(buffer);
	}
	
	private static void split(ByteBuffer from) {
		from.flip();
		for (int i = 0; i < from.limit(); i++) {
			//下标判断分隔位置 不改变position
			if (from.get(i) == '\n') {
				//新建buff 保存完整消息 
				int len = i + 1 - from.position();
				ByteBuffer to = ByteBuffer.allocate(len);
				for (int j = 0; j < len; j++) {
					to.put(from.get());
				}
				debugAll(to);
			}
		}
		//保留未读完的数据
		from.compact();
	}
}
