package com.zkc.chat.client.console;

import com.zkc.chat.util.SessionUtil;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * 管理类中，把所有要管理的控制台指令都放到一个Map中
 */
@Slf4j
public class ConsoleCommandManager implements ConsoleCommand {
	
	private Map<String, ConsoleCommand> consoleCommandMap;
	
	public ConsoleCommandManager() {
		consoleCommandMap = new HashMap<>();
		consoleCommandMap.put("单聊", new SendToUserConsoleCommand());
		consoleCommandMap.put("登出", new LogoutConsoleCommand());
		consoleCommandMap.put("建群", new CreateGroupConsoleCommand());
		consoleCommandMap.put("加群", new JoinGroupConsoleCommand());
		consoleCommandMap.put("退群", new QuitGroupConsoleCommand());
		consoleCommandMap.put("群成员", new ListGroupMembersConsoleCommand());
		consoleCommandMap.put("群聊", new SendToGroupConsoleCommand());
	}
	
	@Override
	public void exec(Scanner scanner, Channel channel) {
		String command = scanner.next();
		if (!SessionUtil.hasLogin(channel)) {
			return;
		}
		ConsoleCommand consoleCommand = consoleCommandMap.get(command);
		if (consoleCommand != null) {
			consoleCommand.exec(scanner, channel);
		} else {
			log.error("无法识别[{}]指令，请重新输入！", command);
		}
	}
}
