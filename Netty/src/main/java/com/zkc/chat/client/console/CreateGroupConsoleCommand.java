package com.zkc.chat.client.console;

import com.zkc.chat.protocol.request.CreateGroupRequestPacket;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.Scanner;

@Slf4j
public class CreateGroupConsoleCommand implements ConsoleCommand {
	
	private static final String USER_ID_SPLITTER = ",";
	
	@Override
	public void exec(Scanner scanner, Channel channel) {
		CreateGroupRequestPacket createGroupRequestPacket = new CreateGroupRequestPacket();
		log.info("【拉人群聊】 输入用户ID列表，用户ID用英文逗号隔开：");
		String userIds = scanner.next();
		createGroupRequestPacket.setUserIdList(Arrays.asList(userIds.split(USER_ID_SPLITTER)));
		channel.writeAndFlush(createGroupRequestPacket);
	}
	
}
