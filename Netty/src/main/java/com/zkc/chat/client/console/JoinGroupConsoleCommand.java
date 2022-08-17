package com.zkc.chat.client.console;

import com.zkc.chat.protocol.request.JoinGroupRequestPacket;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;

import java.util.Scanner;

@Slf4j
public class JoinGroupConsoleCommand implements ConsoleCommand {
	
	@Override
	public void exec(Scanner scanner, Channel channel) {
		log.info("输入groupID 加入群聊：");
		JoinGroupRequestPacket joinGroupRequestPacket = new JoinGroupRequestPacket();
		String groupId = scanner.next();
		joinGroupRequestPacket.setGroupId(groupId);
		channel.writeAndFlush(joinGroupRequestPacket);
	}
	
}
