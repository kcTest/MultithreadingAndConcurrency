package com.zkc.chat.client.console;

import io.netty.channel.Channel;

import java.util.Scanner;

/**
 * 控制台命令执行器接口
 */
public interface ConsoleCommand {
	
	void exec(Scanner scanner, Channel channel);
}
