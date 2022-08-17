package com.zkc.chat.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

@Slf4j
public class IMIdleStateHandler extends IdleStateHandler {
	
	private static final int READER_IDLE_TIME = 15;
	
	/**
	 * 其中第一个参数是读空闲时间，指的是在这段时间内如果没有读到数据，就表示连接假死；
	 * 第二个参数是写空闲时间，指的是在这段时间如果没有写数据，就表示连接假死；
	 * 第三个参数是读写空闲时间，指的是在这段时间内如果没有产生数据读或者写，就表示连接假死，写空闲和读写空闲均为0；
	 * 最后一个参数是时间单位，在这个例子中表示的是：如果15秒内没有读到数据，就表示连接假死
	 */
	public IMIdleStateHandler() {
		super(READER_IDLE_TIME, 0, 0, TimeUnit.SECONDS);
	}
	
	@Override
	protected void channelIdle(ChannelHandlerContext ctx, IdleStateEvent evt) throws Exception {
		log.info("{}秒内未读到数据，关闭连接", READER_IDLE_TIME);
		ctx.channel().close();
	}
}
