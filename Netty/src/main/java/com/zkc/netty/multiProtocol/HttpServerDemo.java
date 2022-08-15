package com.zkc.netty.multiProtocol;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;

import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_LENGTH;

/**
 *
 */
@Slf4j
public class HttpServerDemo {
	
	public static void main(String[] args) {
		NioEventLoopGroup acceptGroup = new NioEventLoopGroup();
		NioEventLoopGroup ioGroup = new NioEventLoopGroup();
		try {
			ChannelFuture channelFuture = new ServerBootstrap()
					.group(acceptGroup, ioGroup)
					.channel(NioServerSocketChannel.class)
					.childHandler(new ChannelInitializer<NioSocketChannel>() {
						@Override
						protected void initChannel(NioSocketChannel ch) throws Exception {
							ch.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG))
									.addLast(new HttpServerCodec())
//									.addLast(new ChannelInboundHandlerAdapter() {
//										@Override
//										public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//											log.debug("{}", msg.getClass());
//										}
//									});
									/*只处理特定类型的请求*/
									.addLast(new SimpleChannelInboundHandler<HttpRequest>() {
										@Override
										protected void channelRead0(ChannelHandlerContext ctx, HttpRequest msg) throws Exception {
											/*打印请求信息并响应*/
											log.debug(msg.uri());
											
											DefaultFullHttpResponse response = new DefaultFullHttpResponse(
													msg.protocolVersion(), HttpResponseStatus.OK);
											byte[] bytes = "<h1>标题</h1><span><b>打印请求信息并响应</b></span>".getBytes(Charset.forName("gbk"));
											/*设置内容长度 否则浏览器不会停止当前请求*/
											response.headers().add(CONTENT_LENGTH, bytes.length);
											response.content().writeBytes(bytes);
											ctx.writeAndFlush(response);
										}
									});
						}
					})
					.bind(new InetSocketAddress(8080))
					.sync();
			
			channelFuture.channel().closeFuture().sync();
		} catch (
				InterruptedException e) {
			log.error("server error: ", e);
		} finally {
			acceptGroup.shutdownGracefully();
			ioGroup.shutdownGracefully();
		}
		
	}
}
