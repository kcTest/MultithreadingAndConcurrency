package com.zkc.chat.client.handler;

import com.zkc.chat.protocol.response.ListGroupMembersResponsePacket;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ListGroupMembersResponseHandler extends SimpleChannelInboundHandler<ListGroupMembersResponsePacket> {
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, ListGroupMembersResponsePacket msg) throws Exception {
		log.info("群[{}]中的人包括：{}", msg.getGroupId(), msg.getSessionList());
	}
}
