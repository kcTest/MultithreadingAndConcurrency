package com.zkc.chat.protocol.request;

import com.zkc.chat.protocol.Packet;
import com.zkc.chat.protocol.command.Command;
import lombok.Data;

@Data
public class ListGroupMembersRequestPacket extends Packet {
	
	private String groupId;
	
	@Override
	public Byte getCommand() {
		return Command.LIST_GROUP_MEMBERS_REQUEST;
	}
}
