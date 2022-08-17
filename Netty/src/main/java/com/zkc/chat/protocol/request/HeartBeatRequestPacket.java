package com.zkc.chat.protocol.request;

import com.zkc.chat.protocol.Packet;
import com.zkc.chat.protocol.command.Command;

public class HeartBeatRequestPacket extends Packet {
	
	@Override
	public Byte getCommand() {
		return Command.HEARTBEAT_REQUEST;
	}
}
