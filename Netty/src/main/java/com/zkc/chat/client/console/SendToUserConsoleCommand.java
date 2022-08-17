package com.zkc.chat.client.console;

import com.zkc.chat.protocol.request.MessageRequestPacket;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;

import java.util.Scanner;

@Slf4j
public class SendToUserConsoleCommand implements ConsoleCommand {
	
	@Override
	public void exec(Scanner scanner, Channel channel) {
		log.info("请输入目标用户id: ");
		String toUserId = scanner.next();
		log.info("请输入消息内容: ");
		String message = scanner.next();
		channel.writeAndFlush(new MessageRequestPacket(toUserId, message));
	}
}
