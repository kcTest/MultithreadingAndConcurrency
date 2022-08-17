package com.zkc.chat.client.handler;

import com.zkc.chat.protocol.response.MessageResponsePacket;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MessageResponseHandler extends SimpleChannelInboundHandler<MessageResponsePacket> {
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, MessageResponsePacket msg) throws Exception {
		String fromUserId = msg.getFromUserId();
		String fromUserName = msg.getFromUserName();
		log.info("{}:{} -> {}", fromUserId, fromUserName, msg.getMessage());
	}
}
