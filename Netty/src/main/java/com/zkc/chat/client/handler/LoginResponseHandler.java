package com.zkc.chat.client.handler;

import com.zkc.chat.client.session.Session;
import com.zkc.chat.protocol.response.LoginResponsePacket;
import com.zkc.chat.util.SessionUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * 客户端处理登录响应
 */
@Slf4j
public class LoginResponseHandler extends SimpleChannelInboundHandler<LoginResponsePacket> {
	
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, LoginResponsePacket msg) throws Exception {
		String userId = msg.getUserId();
		String userName = msg.getUserName();
		if (msg.isSuccess()) {
			log.info("[{}]登录成功,userId为：{}", userName, userId);
			SessionUtil.bindSession(new Session(userId, userName), ctx.channel());
		} else {
			log.error("[{}]登录失败，原因：{}", userName, msg.getReason());
		}
	}
	
	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		log.info("客户端连接被关闭！");
	}
} 
