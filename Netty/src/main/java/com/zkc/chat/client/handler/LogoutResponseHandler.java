package com.zkc.chat.client.handler;

import com.zkc.chat.protocol.response.LogoutResponsePacket;
import com.zkc.chat.util.SessionUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class LogoutResponseHandler extends SimpleChannelInboundHandler<LogoutResponsePacket> {
	
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, LogoutResponsePacket msg) throws Exception {
		SessionUtil.unBindSession(ctx.channel());
	}
}
