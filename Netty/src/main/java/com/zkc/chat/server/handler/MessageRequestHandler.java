package com.zkc.chat.server.handler;

import com.zkc.chat.client.session.Session;
import com.zkc.chat.protocol.request.MessageRequestPacket;
import com.zkc.chat.protocol.response.MessageResponsePacket;
import com.zkc.chat.util.SessionUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * 收到一个客户端的消息之后，构建一条发送给另一个客户端的消息，
 * 获得另一个客户端的Channel然后通过writeAndFlush()写出
 */
@Slf4j
@ChannelHandler.Sharable
public class MessageRequestHandler extends SimpleChannelInboundHandler<MessageRequestPacket> {
	
	public static final MessageRequestHandler INSTANCE = new MessageRequestHandler();
	
	private MessageRequestHandler() {
	}
	
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, MessageRequestPacket msg) throws Exception {
		//1、拿到消息发送方的会话信息
		Session session = SessionUtil.getSession(ctx.channel());
		//2、通过消息发送方的会话消息构造要发送的消息
		MessageResponsePacket messageResponsePacket = new MessageResponsePacket();
		messageResponsePacket.setFromUserId(session.getUserId());
		messageResponsePacket.setFromUserName(session.getUserName());
		messageResponsePacket.setMessage(msg.getMessage());
		//3、拿到消息接收方的channel
		Channel toUserChannel = SessionUtil.getChannel(msg.getToUserId());
		//3、将消息发送给消息接收方
		if (toUserChannel != null && SessionUtil.hasLogin(toUserChannel)) {
			toUserChannel.writeAndFlush(messageResponsePacket).addListener(future -> {
				if (future.isDone()) {
				}
			});
		} else {
			log.warn("[{}] 不在线, 发送失败！", session.getUserId());
		}
	}
}
