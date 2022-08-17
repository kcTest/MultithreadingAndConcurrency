package com.zkc.chat.protocol.response;

import com.zkc.chat.protocol.Packet;
import com.zkc.chat.protocol.command.Command;
import lombok.Data;

@Data
public class JoinGroupResponsePacket extends Packet {
	
	private String groupId;
	private boolean success;
	private String reason;
	
	@Override
	public Byte getCommand() {
		return Command.JOIN_GROUP_RESPONSE;
	}
}
