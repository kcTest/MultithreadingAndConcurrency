package com.zkc.chat.util;

import com.zkc.chat.attribute.Attributes;
import com.zkc.chat.client.session.Session;
import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class SessionUtil {
	
	private static final Map<String, Channel> userIdChannelMap = new ConcurrentHashMap<>();
	private static final Map<String, ChannelGroup> groupChannelGroupMap = new ConcurrentHashMap<>();
	
	public static void bindSession(Session session, Channel channel) {
		userIdChannelMap.put(session.getUserId(), channel);
		channel.attr(Attributes.SESSION).set(session);
	}
	
	public static void unBindSession(Channel channel) {
		if (hasLogin(channel)) {
			Session session = getSession(channel);
			userIdChannelMap.remove(session.getUserId());
			channel.attr(Attributes.SESSION).set(null);
			log.info("{}退出登录", session);
		}
	}
	
	/**
	 * 只判断当前是否有用户的会话信息
	 */
	public static boolean hasLogin(Channel channel) {
		return getSession(channel) != null;
	}
	
	public static Session getSession(Channel channel) {
		return channel.attr(Attributes.SESSION).get();
	}
	
	public static Channel getChannel(String userId) {
		return userIdChannelMap.get(userId);
	}
	
	/**
	 * 把多个Channel聚合在一起，可以对Channel进行批量操作
	 */
	public static void bindChannelGroup(String groupId, ChannelGroup channelGroup) {
		groupChannelGroupMap.put(groupId, channelGroup);
	}
	
	public static ChannelGroup getChannelGroup(String groupId) {
		return groupChannelGroupMap.get(groupId);
	}
}
