package com.zkc.chat.client.handler;

import com.zkc.chat.protocol.response.CreateGroupResponsePacket;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CreateGroupResponsehandler extends SimpleChannelInboundHandler<CreateGroupResponsePacket> {
	
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, CreateGroupResponsePacket msg) throws Exception {
		log.info("群创建成功,id为[{}]", msg.getGroupId());
		log.info("群里面有:{}", msg.getUserNameList());
	}
}
