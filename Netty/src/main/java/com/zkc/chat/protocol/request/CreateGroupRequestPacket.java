package com.zkc.chat.protocol.request;

import com.zkc.chat.protocol.Packet;
import com.zkc.chat.protocol.command.Command;
import lombok.Data;

import java.util.List;

@Data
public class CreateGroupRequestPacket extends Packet {
	
	private List<String> userIdList;
	
	@Override
	public Byte getCommand() {
		return Command.CREATE_GROUP_REQUEST;
	}
}
