package com.zkc.chat.client.console;

import com.zkc.chat.protocol.request.MessageRequestPacket;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;

import java.util.Scanner;

@Slf4j
public class SendToUserConsoleCommand implements ConsoleCommand {
	
	@Override
	public void exec(Scanner scanner, Channel channel) {
		log.info("发送消息给某个用户: ");
		String toUserId = scanner.next();
		String message = scanner.next();
		channel.writeAndFlush(new MessageRequestPacket(toUserId, message));
	}
}
