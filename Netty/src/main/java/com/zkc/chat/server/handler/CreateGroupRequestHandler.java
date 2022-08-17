package com.zkc.chat.server.handler;

import com.zkc.chat.protocol.request.CreateGroupRequestPacket;
import com.zkc.chat.protocol.response.CreateGroupResponsePacket;
import com.zkc.chat.util.IDUtil;
import com.zkc.chat.util.SessionUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.DefaultChannelGroup;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@ChannelHandler.Sharable
public class CreateGroupRequestHandler extends SimpleChannelInboundHandler<CreateGroupRequestPacket> {
	
	public static final CreateGroupRequestHandler INSTANCE = new CreateGroupRequestHandler();
	
	private CreateGroupRequestHandler() {
	}
	
	
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, CreateGroupRequestPacket msg) throws Exception {
		List<String> userIdList = msg.getUserIdList();
		
		List<String> userNameList = new ArrayList<>();
		//1、创建一个channel分组
		DefaultChannelGroup channelGroup = new DefaultChannelGroup(ctx.executor());
		//2、筛选出待加入群聊的用户的channel和userName
		for (String userId : userIdList) {
			Channel channel = SessionUtil.getChannel(userId);
			if (channel != null) {
				channelGroup.add(channel);
				userNameList.add(SessionUtil.getSession(channel).getUserName());
			}
		}
		//3、构造群聊创建结果的响应
		String groupId = IDUtil.randomId();
		CreateGroupResponsePacket createGroupResponsePacket = new CreateGroupResponsePacket();
		createGroupResponsePacket.setSuccess(true);
		createGroupResponsePacket.setGroupId(groupId);
		createGroupResponsePacket.setUserNameList(userNameList);
		//4、给每个客户端发送拉群通知
		channelGroup.writeAndFlush(createGroupResponsePacket);
		//5、保存群组相关的信息
		SessionUtil.bindChannelGroup(groupId, channelGroup);
		
		log.info("群创建成功，id为：{}", createGroupResponsePacket.getGroupId());
		log.info("群里面有：{}", createGroupResponsePacket.getUserNameList());
	}
}
