package com.zkc.chat.server.handler;

import com.zkc.chat.util.SessionUtil;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

@ChannelHandler.Sharable
public class AuthHandler extends ChannelInboundHandlerAdapter {
	
	public static final AuthHandler INSTANCE = new AuthHandler();
	
	private AuthHandler() {
	}
	
	/**
	 * 在客户端校验通过之后不再需要AuthHandler这段逻辑
	 * 直接调用Pipeline的remove()方法删除自身，
	 */
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		if (!SessionUtil.hasLogin(ctx.channel())) {
			ctx.channel().close();
		} else {
			ctx.pipeline().remove(this);
			super.channelRead(ctx, msg);
		}
	}
}
