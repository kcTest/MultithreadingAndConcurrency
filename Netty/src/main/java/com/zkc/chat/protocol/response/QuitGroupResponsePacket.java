package com.zkc.chat.protocol.response;

import com.zkc.chat.protocol.Packet;
import com.zkc.chat.protocol.command.Command;
import lombok.Data;

@Data
public class QuitGroupResponsePacket extends Packet {
	
	private String groupId;
	private boolean success;
	private String reason;
	
	@Override
	public Byte getCommand() {
		return Command.QUIT_GROUP_RESPONSE;
	}
}
