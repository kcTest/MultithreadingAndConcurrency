package com.zkc.example;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.channel.embedded.EmbeddedChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;

/**
 * 单元测试:以尽可能小的区块测试你的代码，并且尽可能地和其他的代码模块以及运行时的依赖（如数据库和网
 * 络）相隔离。如果你的应用程序能通过测试验证每个单元本身都能够正常地工作，那么在出了问
 * 题时将可以更加容易地找出根本原因
 * <p>
 * 使用 EmbeddedChannel 来测试 ChannelHandler
 */
public class EmbeddedChannelDemo {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(EmbeddedChannelDemo.class);
	
	public static void main(String[] args) {
		
		ChannelInboundHandlerAdapter cih1 = new ChannelInboundHandlerAdapter() {
			@Override
			public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
				ByteBuf buf = (ByteBuf) msg;
				LOGGER.debug("CIH1 data：{}", buf.toString(StandardCharsets.UTF_8));
				super.channelRead(ctx, msg);
			}
		};
		
		ChannelInboundHandlerAdapter cih2 = new ChannelInboundHandlerAdapter() {
			@Override
			public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
				ByteBuf buf = (ByteBuf) msg;
				LOGGER.debug("CIH1 data：{}", buf.toString(StandardCharsets.UTF_8));
				super.channelRead(ctx, msg);
			}
		};
		
		ChannelOutboundHandlerAdapter coh1 = new ChannelOutboundHandlerAdapter() {
			@Override
			public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
				LOGGER.debug("COH1 data：{}", msg);
				super.write(ctx, msg, promise);
			}
		};
		
		ChannelOutboundHandlerAdapter coh2 = new ChannelOutboundHandlerAdapter() {
			@Override
			public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
				LOGGER.debug("COH2 data：{}", msg);
				super.write(ctx, msg, promise);
			}
		};
		
		EmbeddedChannel embeddedChannel = new EmbeddedChannel(cih1, cih2, coh1, coh2);
		/*模拟入站操作*/
		embeddedChannel.writeInbound(ByteBufAllocator.DEFAULT.buffer().writeBytes("writeInbound data".getBytes(StandardCharsets.UTF_8)));
		/*模拟出站操作*/
		embeddedChannel.writeOutbound(ByteBufAllocator.DEFAULT.buffer().writeBytes("writeOutbound data".getBytes(StandardCharsets.UTF_8)));
	}
}
