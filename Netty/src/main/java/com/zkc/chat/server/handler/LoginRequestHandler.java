package com.zkc.chat.server.handler;

import com.zkc.chat.protocol.request.LoginRequestPacket;
import com.zkc.chat.client.session.Session;
import com.zkc.chat.protocol.response.LoginResponsePacket;
import com.zkc.chat.util.IDUtil;
import com.zkc.chat.util.SessionUtil;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

@ChannelHandler.Sharable
@Slf4j
public class LoginRequestHandler extends SimpleChannelInboundHandler<LoginRequestPacket> {
	
	public static final LoginRequestHandler INSTANCE = new LoginRequestHandler();
	
	protected LoginRequestHandler() {
	}
	
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, LoginRequestPacket msg) throws Exception {
		LoginResponsePacket loginResponsePacket = new LoginResponsePacket();
		loginResponsePacket.setVersion(msg.getVersion());
		String userName = msg.getUserName();
		loginResponsePacket.setUserName(userName);
		
		if (valid(msg)) {
			loginResponsePacket.setSuccess(true);
			String userId = IDUtil.randomId();
			loginResponsePacket.setUserId(userId);
			log.info("[{}]登录成功", userName);
			SessionUtil.bindSession(new Session(userId, userName), ctx.channel());
		} else {
			loginResponsePacket.setSuccess(false);
			loginResponsePacket.setReason("账号密码校验失败");
			log.info("登录失败！");
		}
		
		ctx.writeAndFlush(loginResponsePacket);
	}
	
	/**
	 * 假设所有的登录都是成功的
	 */
	private boolean valid(LoginRequestPacket loginRequestPacket) {
		return true;
	}
	
	/**
	 * 用户下线后删除userId到Channel的映射关系
	 */
	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		SessionUtil.unBindSession(ctx.channel());
	}
}
