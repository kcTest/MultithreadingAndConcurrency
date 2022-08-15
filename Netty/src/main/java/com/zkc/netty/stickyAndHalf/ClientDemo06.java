package com.zkc.netty.stickyAndHalf;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

import static com.zkc.netty.util.BufUtil.logBB;

/**
 * 2、定长解码器 解决半包黏包 双方约定每条消息最大长度 每条消息长度不固定 可能造成资源浪费
 */
@Slf4j
public class ClientDemo06 {
	
	public static void main(String[] args) {
		NioEventLoopGroup ioGroup = new NioEventLoopGroup();
		try {
			ChannelFuture channelFuture = new Bootstrap()
					.group(ioGroup)
					.channel(NioSocketChannel.class)
					.handler(new ChannelInitializer<NioSocketChannel>() {
						@Override
						protected void initChannel(NioSocketChannel ch) throws Exception {
							ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {
								@Override
								public void channelActive(ChannelHandlerContext ctx) throws Exception {
									/*默认出现黏包问题（暂不调整服务端缓冲区大小）*/
									sendFixedLenMsg(ctx);
								}
							});
						}
					})
					.connect(new InetSocketAddress("127.0.0.1", 8080))
					.sync();
			
			channelFuture.channel().closeFuture().sync();
		} catch (InterruptedException e) {
			log.error("client error: ");
		} finally {
			ioGroup.shutdownGracefully();
		}
	}
	
	/**
	 * 单次发送一条随机长度的消息 每条消息最大长度N，不足N的地方用特殊符号填充
	 */
	private static void sendFixedLenMsg(ChannelHandlerContext ctx) {
		for (int i = 0; i < 5; i++) {
			ByteBuf buf = ctx.alloc().buffer(10);
			int msgLen = (int) (Math.random() * 10) + 1;
			for (int j = 0; j < msgLen; j++) {
				buf.writeByte((int) (Math.random() * 26) + 97);
			}
			//最后俩个不填充 检查服务器对不足10个的地方处理 ：不处理，先保留等服务端收够10个字节时才会解码
			if (msgLen < 10 && i < 5 - 2) {
				for (int j = msgLen; j < 10; j++) {
					buf.writeByte('_');
				}
			}
			logBB(buf);
			ctx.writeAndFlush(buf);
		}
	}
	
}
