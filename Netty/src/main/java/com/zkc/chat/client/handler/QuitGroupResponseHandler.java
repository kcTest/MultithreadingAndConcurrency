package com.zkc.chat.client.handler;

import com.zkc.chat.protocol.response.QuitGroupResponsePacket;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class QuitGroupResponseHandler extends SimpleChannelInboundHandler<QuitGroupResponsePacket> {
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, QuitGroupResponsePacket msg) throws Exception {
		if (msg.isSuccess()) {
			log.info("退出群聊[{}]成功！", msg.getGroupId());
		} else {
			log.info("退出群聊[{}]失败！", msg.getGroupId());
		}
	}
}
