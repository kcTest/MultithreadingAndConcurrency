package com.zkc.chat.protocol.request;

import com.zkc.chat.protocol.Packet;
import com.zkc.chat.protocol.command.Command;
import lombok.Data;

@Data
public class JoinGroupRequestPacket extends Packet {
	
	private String groupId;
	
	@Override
	public Byte getCommand() {
		return Command.JOIN_GROUP_REQUEST;
	}
}
