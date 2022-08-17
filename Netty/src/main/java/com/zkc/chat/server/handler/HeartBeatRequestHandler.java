package com.zkc.chat.server.handler;

import com.zkc.chat.protocol.request.HeartBeatRequestPacket;
import com.zkc.chat.protocol.response.HeartBeatResponsePacket;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

@ChannelHandler.Sharable
public class HeartBeatRequestHandler extends SimpleChannelInboundHandler<HeartBeatRequestPacket> {
	
	public static final HeartBeatRequestHandler INSTANCE = new HeartBeatRequestHandler();
	
	private HeartBeatRequestHandler() {
	}
	
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, HeartBeatRequestPacket msg) throws Exception {
		ctx.writeAndFlush(new HeartBeatResponsePacket());
	}
}
