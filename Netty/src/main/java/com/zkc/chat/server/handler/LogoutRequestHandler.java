package com.zkc.chat.server.handler;

import com.zkc.chat.protocol.request.LogoutRequestPacket;
import com.zkc.chat.protocol.response.LogoutResponsePacket;
import com.zkc.chat.util.SessionUtil;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

@ChannelHandler.Sharable
public class LogoutRequestHandler extends SimpleChannelInboundHandler<LogoutRequestPacket> {
	
	public static final LogoutRequestHandler INSTANCE = new LogoutRequestHandler();
	
	private LogoutRequestHandler() {
	}
	
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, LogoutRequestPacket msg) throws Exception {
		SessionUtil.unBindSession(ctx.channel());
		LogoutResponsePacket logoutResponsePacket = new LogoutResponsePacket();
		logoutResponsePacket.setSuccess(true);
		ctx.writeAndFlush(logoutResponsePacket);
	}
}
