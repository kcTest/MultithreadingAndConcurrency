package com.zkc.chat.client.handler;

import com.zkc.chat.client.session.Session;
import com.zkc.chat.protocol.response.GroupMessageResponsePacket;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GroupMessageResponseHandler extends SimpleChannelInboundHandler<GroupMessageResponsePacket> {
	
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, GroupMessageResponsePacket msg) throws Exception {
		String fromGroupId = msg.getFromGroupId();
		Session fromUser = msg.getFromUser();
		log.info("收到群[{}]中[{}]发来的消息：{}", fromGroupId, fromUser, msg.getMessage());
	}
}
