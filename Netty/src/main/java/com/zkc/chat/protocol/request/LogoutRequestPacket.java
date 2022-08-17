package com.zkc.chat.protocol.request;

import com.zkc.chat.protocol.Packet;
import com.zkc.chat.protocol.command.Command;
import lombok.Data;

@Data
public class LogoutRequestPacket extends Packet {
	
	@Override
	public Byte getCommand() {
		return Command.LOGOUT_REQUEST;
	}
}
