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
				/* 2、 创建并分配一个 NioEventLoopGroup 实例以进行事件的处理，如接受新连接以及读/写数据
				       EventLoopGroup包含一个或者多个EventLoop   
				       一个EventLoop本质是一个单线程执行器（NioEventLoop:selector+thread) 可能服务多个Channel */
				.group(new NioEventLoopGroup())
				/* 3、选择服务端使用的通道实现 支持NIO BIO。 NioServerSocketChannel：基于NIO的ServerSocketChannel的实现 */
				.channel(NioServerSocketChannel.class)
				/* 4、childHandler()：SocketChannel使用，设置与客户端通道相关的事件 
				handler()：ServerSocketChannel使用  设置与服务端通道相关的事件*/
				.childHandler(
						/*5、连接建立后  开始初始化 负责对已连接客户端进行数据读写的NioSocketChannel（sc）*/
						new ChannelInitializer<NioSocketChannel>() {
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
