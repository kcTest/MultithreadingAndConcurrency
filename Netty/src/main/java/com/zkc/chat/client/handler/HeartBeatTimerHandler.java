package com.zkc.chat.client.handler;

import com.zkc.chat.protocol.request.HeartBeatRequestPacket;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.concurrent.TimeUnit;

public class HeartBeatTimerHandler extends ChannelInboundHandlerAdapter {
	
	private static final int HEARTBEAT_INTERVAL = 5;
	
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		scheduleSendHeatBeat(ctx);
		super.channelActive(ctx);
	}
	
	private void scheduleSendHeatBeat(ChannelHandlerContext ctx) {
		ctx.executor().schedule(() -> {
			if (ctx.channel().isActive()) {
				ctx.writeAndFlush(new HeartBeatRequestPacket());
				scheduleSendHeatBeat(ctx);
			}
		}, HEARTBEAT_INTERVAL, TimeUnit.SECONDS);
	}
}
