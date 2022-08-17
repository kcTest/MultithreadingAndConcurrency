package com.zkc.chat.protocol;

import com.zkc.chat.protocol.request.*;
import com.zkc.chat.protocol.response.*;
import com.zkc.chat.serializer.Serializer;
import com.zkc.chat.serializer.impl.JsonSerializer;
import io.netty.buffer.ByteBuf;

import java.util.HashMap;
import java.util.Map;

import static com.zkc.chat.protocol.command.Command.*;

/**
 * 编解码
 * 【魔数 4】【版本号 1】【序列化算法 1】【指令 1】【消息数据长度 4】【消息数据 N】
 */
public class PacketCodeC {
	
	public static final int MAGIC_NUMBER = 0X123321;
	
	public static final PacketCodeC INSTANCE = new PacketCodeC();
	
	private final Map<Byte, Class<? extends Packet>> packetTypeMap;
	private final Map<Byte, Serializer> serializerMap;
	
	private PacketCodeC() {
		packetTypeMap = new HashMap<>();
		packetTypeMap.put(LOGIN_REQUEST, LoginRequestPacket.class);
		packetTypeMap.put(LOGIN_RESPONSE, LoginResponsePacket.class);
		
		packetTypeMap.put(MESSAGE_REQUEST, MessageRequestPacket.class);
		packetTypeMap.put(MESSAGE_RESPONSE, MessageResponsePacket.class);
		
		packetTypeMap.put(LOGOUT_REQUEST, LogoutRequestPacket.class);
		packetTypeMap.put(LOGOUT_RESPONSE, LogoutResponsePacket.class);
		
		packetTypeMap.put(CREATE_GROUP_REQUEST, CreateGroupRequestPacket.class);
		packetTypeMap.put(CREATE_GROUP_RESPONSE, CreateGroupResponsePacket.class);
		
		packetTypeMap.put(JOIN_GROUP_REQUEST, JoinGroupRequestPacket.class);
		packetTypeMap.put(JOIN_GROUP_RESPONSE, JoinGroupResponsePacket.class);
		
		packetTypeMap.put(QUIT_GROUP_REQUEST, QuitGroupRequestPacket.class);
		packetTypeMap.put(QUIT_GROUP_RESPONSE, QuitGroupResponsePacket.class);
		
		packetTypeMap.put(LIST_GROUP_MEMBERS_REQUEST, ListGroupMembersRequestPacket.class);
		packetTypeMap.put(LIST_GROUP_MEMBERS_RESPONSE, ListGroupMembersResponsePacket.class);
		
		packetTypeMap.put(GROUP_MESSAGE_REQUEST, GroupMessageRequestPacket.class);
		packetTypeMap.put(GROUP_MESSAGE_RESPONSE, GroupMessageResponsePacket.class);
		
		packetTypeMap.put(HEARTBEAT_REQUEST, HeartBeatRequestPacket.class);
		packetTypeMap.put(HEARTBEAT_RESPONSE, HeartBeatResponsePacket.class);
		
		serializerMap = new HashMap<>();
		JsonSerializer jsonSerializer = new JsonSerializer();
		serializerMap.put(jsonSerializer.getSerializerAlgorithm(), jsonSerializer);
	}
	
	/**
	 * 消息发送前 按协议要求填充各部分数据并转成二进制数据
	 */
	public void encode(ByteBuf buf, Packet packet) {
		//创建序列化java对象
		byte[] bytes = Serializer.DEFAULT.serializer(packet);
		
		//填充并转成二进制数据
		buf.writeInt(MAGIC_NUMBER);
		buf.writeByte(packet.getVersion());
		buf.writeByte(Serializer.DEFAULT.getSerializerAlgorithm());
		buf.writeByte(packet.getCommand());
		buf.writeInt(bytes.length);
		buf.writeBytes(bytes);
	}
	
	/**
	 * 接收ByteBuf后 按协议提取各部分数据
	 */
	public Packet decode(ByteBuf buf) {
		//跳过魔数
		buf.skipBytes(4);
		//跳过版本号
		buf.skipBytes(1);
		//序列化算法标识
		byte serializerAlgorithm = buf.readByte();
		//指令
		byte command = buf.readByte();
		//消息数据长度
		int length = buf.readInt();
		//消息数据
		byte[] bytes = new byte[length];
		buf.readBytes(bytes);
		
		//反序列化
		Class<? extends Packet> requestType = getRequestType(command);
		Serializer serializer = getSerializer(serializerAlgorithm);
		if (requestType != null && serializer != null) {
			return serializer.deserializer(bytes, requestType);
		}
		return null;
	}
	
	private Class<? extends Packet> getRequestType(byte command) {
		return packetTypeMap.get(command);
	}
	
	private Serializer getSerializer(byte serializerAlgorithm) {
		return serializerMap.get(serializerAlgorithm);
	}
}
