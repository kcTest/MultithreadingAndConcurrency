package com.zkc.chat.server.handler;

import com.zkc.chat.protocol.Packet;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.HashMap;
import java.util.Map;

import static com.zkc.chat.protocol.command.Command.*;

/**
 * 压缩Handler
 */
@ChannelHandler.Sharable
public class IMHandler extends SimpleChannelInboundHandler<Packet> {
	
	public static final IMHandler INSTANCE = new IMHandler();
	/**
	 * 存放指令到各个指令处理器的映射
	 */
	private Map<Byte, SimpleChannelInboundHandler<? extends Packet>> handlerMap;
	
	private IMHandler() {
		handlerMap = new HashMap<>();
		//无状态 可以被channel共享 写成单例
		handlerMap.put(MESSAGE_REQUEST, MessageRequestHandler.INSTANCE);
		handlerMap.put(CREATE_GROUP_REQUEST, CreateGroupRequestHandler.INSTANCE);
		handlerMap.put(JOIN_GROUP_REQUEST, JoinGroupRequestHandler.INSTANCE);
		handlerMap.put(QUIT_GROUP_REQUEST, QuitGroupRequestHandler.INSTANCE);
		handlerMap.put(LIST_GROUP_MEMBERS_REQUEST, ListGroupMembersRequestHandler.INSTANCE);
		handlerMap.put(GROUP_MESSAGE_REQUEST, GroupMessageRequestHandler.INSTANCE);
		handlerMap.put(LOGOUT_REQUEST, LogoutRequestHandler.INSTANCE);
	}
	
	/**
	 * 每次回调IMHandler的channelRead0()方法的时候，都通过指令找到具体的Handler，
	 * 然后调用指令Handler的channelRead()方法，其内部会进行指令类型转换，最终调用每个指令Handler的channelRead0()方法。
	 */
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Packet msg) throws Exception {
		handlerMap.get(msg.getCommand()).channelRead(ctx, msg);
	}
}
