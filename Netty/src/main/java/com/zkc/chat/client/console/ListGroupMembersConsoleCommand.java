package com.zkc.chat.client.console;

import com.zkc.chat.protocol.request.ListGroupMembersRequestPacket;
import com.zkc.chat.protocol.request.QuitGroupRequestPacket;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;

import java.util.Scanner;

@Slf4j
public class ListGroupMembersConsoleCommand implements ConsoleCommand {
	
	@Override
	public void exec(Scanner scanner, Channel channel) {
		log.info("输入groupID 获取群成员列表：");
		ListGroupMembersRequestPacket listGroupMembersRequestPacket = new ListGroupMembersRequestPacket();
		String groupId = scanner.next();
		listGroupMembersRequestPacket.setGroupId(groupId);
		channel.writeAndFlush(listGroupMembersRequestPacket);
	}
	
}
