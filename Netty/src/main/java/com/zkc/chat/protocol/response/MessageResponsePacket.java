package com.zkc.chat.protocol.response;

import com.zkc.chat.protocol.Packet;
import com.zkc.chat.protocol.command.Command;
import lombok.Data;

@Data
public class MessageResponsePacket extends Packet {
	
	private String fromUserId;
	private String fromUserName;
	private String message;
	
	@Override
	public Byte getCommand() {
		return Command.MESSAGE_RESPONSE;
	}
}
