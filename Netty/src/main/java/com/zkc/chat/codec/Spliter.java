package com.zkc.chat.codec;

import com.zkc.chat.protocol.PacketCodeC;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

/**
 * 【魔数 4】【版本号 1】【序列化算法 1】【指令 1】【消息数据长度 4】【消息数据 N】
 */
public class Spliter extends LengthFieldBasedFrameDecoder {
	
	private static final int LENGTH_FIELD_OFFSET = 7;
	private static final int LENGTH_FIELD_LENGTH = 4;
	
	public Spliter() {
		super(Integer.MAX_VALUE, LENGTH_FIELD_OFFSET, LENGTH_FIELD_LENGTH);
	}
	
	/**
	 * 尽早屏蔽非本协议的客户端
	 */
	@Override
	protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
		if (in.getInt(in.readerIndex()) != PacketCodeC.MAGIC_NUMBER) {
			ctx.channel().close();
			return null;
		}
		return super.decode(ctx, in);
	}
}
