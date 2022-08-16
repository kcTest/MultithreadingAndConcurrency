package com.zkc.chat;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

@Slf4j
public class LoginRequestHandler extends ChannelInboundHandlerAdapter {
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		log.info("客户端开始登录");
		
		//创建登录请求对象
		LoginRequestPacket loginRequestPacket = new LoginRequestPacket();
		loginRequestPacket.setUserId(UUID.randomUUID().toString());
		loginRequestPacket.setUserName("zkc");
		loginRequestPacket.setPassword("pwd");
		
//		//编码
//		new PacketCodeC.INSTANCE.encode(ctx.alloc(), loginRequestPacket);
//		
//		//写数据
//		ctx.channel().writeAndFlush(buf);
	}
}
