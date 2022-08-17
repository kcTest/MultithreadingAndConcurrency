package com.zkc.chat.protocol.response;

import com.zkc.chat.protocol.Packet;
import com.zkc.chat.protocol.command.Command;

public class HeartBeatResponsePacket extends Packet {
	
	@Override
	public Byte getCommand() {
		return Command.HEARTBEAT_RESPONSE;
	}
}
