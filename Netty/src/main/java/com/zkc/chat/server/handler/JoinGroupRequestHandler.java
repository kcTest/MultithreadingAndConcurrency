package com.zkc.chat.server.handler;

import com.zkc.chat.protocol.request.JoinGroupRequestPacket;
import com.zkc.chat.protocol.response.JoinGroupResponsePacket;
import com.zkc.chat.util.SessionUtil;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;

@ChannelHandler.Sharable
public class JoinGroupRequestHandler extends SimpleChannelInboundHandler<JoinGroupRequestPacket> {
	
	public static final JoinGroupRequestHandler INSTANCE = new JoinGroupRequestHandler();
	
	private JoinGroupRequestHandler() {
	}
	
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, JoinGroupRequestPacket msg) throws Exception {
		//1、获取群对应的 channelGroup 然后将当前用户的channel添加进去
		String groupId = msg.getGroupId();
		ChannelGroup channelGroup = SessionUtil.getChannelGroup(groupId);
		channelGroup.add(ctx.channel());
		//2、构造加群响应发送给客户端
		JoinGroupResponsePacket responsePacket = new JoinGroupResponsePacket();
		responsePacket.setSuccess(true);
		responsePacket.setGroupId(groupId);
		ctx.writeAndFlush(responsePacket);
	}
	
}
