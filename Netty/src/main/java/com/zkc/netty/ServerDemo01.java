package com.zkc.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerDemo01 {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ServerDemo01.class);
	
	public static void main(String[] args) {
		/* 1、启动器：负责组装Netty组件 启动服务器 */
		new ServerBootstrap()
				/* 2、设置EventLoopGroup  包含一个或者多个 EventLoop   由selector+thread组成
				  一个 EventLoop可能会被分配给一个或多个Channel 处理已注册的channel的相关事件 */
				.group(new NioEventLoopGroup())
				/* 3、选择使用的通道 支持NIO BIO。 NioServerSocketChannel：基于NIO的ServerSocketChannel的实现 */
				.channel(NioServerSocketChannel.class)
				/* 4、指定worker要做的事   5、初始化 负责对已连接客户端进行数据读写的 NioSocketChannel（sc）；添加handler */
				.childHandler(new ChannelInitializer<NioSocketChannel>() {
					@Override
					protected void initChannel(NioSocketChannel ch) throws Exception {
						/* 6、添加具体的事件处理器 */
						ch.pipeline()
								//StringDecoder: 将Netty的ByteBuf转为String
								.addLast(new StringDecoder())
								// ChannelInboundHandlerAdapter：处理入站数据以及各种状态变化
								.addLast(new ChannelInboundHandlerAdapter() {
									//传入消息时被调用
									@Override
									public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
										//消息已被StringDecoder转换
										LOGGER.info(msg.toString());
									}
								});
					}
				})
				/* 7、绑定监听端口 */
				.bind(8080);
	}
}
