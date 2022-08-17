package com.zkc.chat.server.handler;

import com.zkc.chat.protocol.request.GroupMessageRequestPacket;
import com.zkc.chat.protocol.response.GroupMessageResponsePacket;
import com.zkc.chat.util.SessionUtil;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;

@ChannelHandler.Sharable
public class GroupMessageRequestHandler extends SimpleChannelInboundHandler<GroupMessageRequestPacket> {
	
	public static final GroupMessageRequestHandler INSTANCE = new GroupMessageRequestHandler();
	
	private GroupMessageRequestHandler() {
	}
	
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, GroupMessageRequestPacket msg) throws Exception {
		//1、拿到groupId构造群聊消息的响应
		String groupId = msg.getToGroupId();
		GroupMessageResponsePacket groupMessageResponsePacket = new GroupMessageResponsePacket();
		groupMessageResponsePacket.setFromGroupId(groupId);
		groupMessageResponsePacket.setMessage(msg.getMessage());
		groupMessageResponsePacket.setFromUser(SessionUtil.getSession(ctx.channel()));
		//2、拿到群聊对应的channelGroup 写到每个客户端
		ChannelGroup channelGroup = SessionUtil.getChannelGroup(groupId);
		channelGroup.writeAndFlush(groupMessageResponsePacket);
	}
}
