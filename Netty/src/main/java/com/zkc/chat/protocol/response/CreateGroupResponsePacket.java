package com.zkc.chat.protocol.response;

import com.zkc.chat.protocol.Packet;
import com.zkc.chat.protocol.command.Command;
import lombok.Data;

import java.util.List;

@Data
public class CreateGroupResponsePacket  extends Packet {
	
	private boolean success;
	private String groupId;
	private List<String> userNameList;
	
	@Override
	public Byte getCommand() {
		return Command.CREATE_GROUP_RESPONSE;
	}
}
