package com.zkc.chat;

import lombok.Data;

/**
 * 客户端与服务端通信的Java对象
 */
@Data
public abstract class Packet {
	/**
	 * 协议版本号
	 */
	private Byte version = 1;
	
	/**
	 * 指令
	 */
	public abstract Byte getCommand();
}
