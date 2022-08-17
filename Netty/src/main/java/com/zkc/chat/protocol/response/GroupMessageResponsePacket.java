package com.zkc.chat.protocol.response;

import com.zkc.chat.protocol.Packet;
import com.zkc.chat.client.session.Session;
import com.zkc.chat.protocol.command.Command;
import lombok.Data;

@Data
public class GroupMessageResponsePacket extends Packet {
	
	private String fromGroupId;
	private Session fromUser;
	private String message;
	
	@Override
	public Byte getCommand() {
		return Command.GROUP_MESSAGE_RESPONSE;
	}
}
