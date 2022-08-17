package com.zkc.chat.codec;

import com.zkc.chat.protocol.Packet;
import com.zkc.chat.protocol.PacketCodeC;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;

import java.util.List;

/**
 * 将编解码操作放到一个类中去实现
 * 一个Handler要被多个Channel共享，必须加上@ChannelHandler.Sharable显式地告诉Netty，这个Handler是支持多个Channel共享的
 */
@ChannelHandler.Sharable
public class PacketCodecHandler extends MessageToMessageCodec<ByteBuf, Packet> {
	
	public static final PacketCodecHandler INSTANCE = new PacketCodecHandler();
	
	private PacketCodecHandler() {
	}
	
	@Override
	protected void encode(ChannelHandlerContext ctx, Packet msg, List<Object> out) throws Exception {
		ByteBuf buf = ctx.channel().alloc().ioBuffer();
		PacketCodeC.INSTANCE.encode(buf, msg);
		out.add(buf);
	}
	
	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
		out.add(PacketCodeC.INSTANCE.decode(msg));
	}
	
}
