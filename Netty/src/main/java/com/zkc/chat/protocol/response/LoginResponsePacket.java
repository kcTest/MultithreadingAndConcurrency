package com.zkc.chat.protocol.response;

import com.zkc.chat.protocol.Packet;
import com.zkc.chat.protocol.command.Command;
import lombok.Data;

@Data
public class LoginResponsePacket extends Packet {
	
	private String userId;
	private String userName;
	private boolean success;
	private String reason;
	
	@Override
	public Byte getCommand() {
		return Command.LOGIN_RESPONSE;
	}
}
