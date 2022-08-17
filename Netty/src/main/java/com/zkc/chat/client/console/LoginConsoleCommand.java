package com.zkc.chat.client.console;

import com.zkc.chat.protocol.request.LoginRequestPacket;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;

import java.util.Scanner;

@Slf4j
public class LoginConsoleCommand implements ConsoleCommand {
	
	@Override
	public void exec(Scanner scanner, Channel channel) {
		LoginRequestPacket loginRequestPacket = new LoginRequestPacket();
		log.info("请输入用户名");
		loginRequestPacket.setUserName(scanner.nextLine());
		loginRequestPacket.setPassword("pwd");
		
		//发送登录数据包
		channel.writeAndFlush(loginRequestPacket);
		waitForLoginResponse();
	}
	
	private void waitForLoginResponse() {
		try {
			Thread.sleep(1000);
		} catch (InterruptedException ignored) {
		}
	}
}
