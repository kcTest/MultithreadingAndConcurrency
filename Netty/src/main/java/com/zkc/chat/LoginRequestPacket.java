package com.zkc.chat;

import lombok.Data;

/**
 * 登录请求数据包
 */
@Data
public class LoginRequestPacket extends Packet {
	
	private String userId;
	private String userName;
	private String password;
	
	@Override
	public Byte getCommand() {
		return Command.LOGIN_REQUEST;
	}
}
