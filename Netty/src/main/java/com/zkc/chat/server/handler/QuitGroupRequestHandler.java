package com.zkc.chat.server.handler;

import com.zkc.chat.protocol.request.QuitGroupRequestPacket;
import com.zkc.chat.protocol.response.QuitGroupResponsePacket;
import com.zkc.chat.util.SessionUtil;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;

@ChannelHandler.Sharable
public class QuitGroupRequestHandler extends SimpleChannelInboundHandler<QuitGroupRequestPacket> {
	
	public static final QuitGroupRequestHandler INSTANCE = new QuitGroupRequestHandler();
	
	private QuitGroupRequestHandler() {
	}
	
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, QuitGroupRequestPacket msg) throws Exception {
		//1、获取对应的channelGroup 然后将当前用户的channel移除
		String groupId = msg.getGroupId();
		ChannelGroup channelGroup = SessionUtil.getChannelGroup(groupId);
		channelGroup.remove(ctx.channel());
		//2、构造退群响应发送给客户端
		QuitGroupResponsePacket quitGroupResponsePacket = new QuitGroupResponsePacket();
		quitGroupResponsePacket.setGroupId(msg.getGroupId());
		quitGroupResponsePacket.setSuccess(true);
		ctx.writeAndFlush(quitGroupResponsePacket);
	}
}
