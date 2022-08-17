package com.zkc.chat.protocol.response;

import com.zkc.chat.protocol.Packet;
import com.zkc.chat.client.session.Session;
import com.zkc.chat.protocol.command.Command;
import lombok.Data;

import java.util.List;

@Data
public class ListGroupMembersResponsePacket extends Packet {
	
	private String groupId;
	private List<Session> sessionList;
	
	@Override
	public Byte getCommand() {
		return Command.LIST_GROUP_MEMBERS_RESPONSE;
	}
}
