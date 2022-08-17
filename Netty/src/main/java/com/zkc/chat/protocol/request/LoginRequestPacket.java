package com.zkc.chat.protocol.request;

import com.zkc.chat.protocol.command.Command;
import com.zkc.chat.protocol.Packet;
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
