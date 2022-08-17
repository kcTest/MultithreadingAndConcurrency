package com.zkc.chat.client.console;

import com.zkc.chat.protocol.request.JoinGroupRequestPacket;
import com.zkc.chat.protocol.request.QuitGroupRequestPacket;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;

import java.util.Scanner;

@Slf4j
public class QuitGroupConsoleCommand implements ConsoleCommand {
	
	@Override
	public void exec(Scanner scanner, Channel channel) {
		log.info("输入groupID 退出群聊：");
		QuitGroupRequestPacket quitGroupRequestPacket = new QuitGroupRequestPacket();
		String groupId = scanner.next();
		quitGroupRequestPacket.setGroupId(groupId);
		channel.writeAndFlush(quitGroupRequestPacket);
	}
	
}
