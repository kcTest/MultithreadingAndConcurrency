package com.zkc.example.bytebuf;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.CompositeByteBuf;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import static com.zkc.example.util.BufUtil.logBB;

/**
 * zero copy
 * slice duplicate/composite Unpooled.wrappedBuffer()
 */
@Slf4j
public class ByteBufDemo02 {
	
	@Test
	public void compositeTest() {
		ByteBuf buf1 = ByteBufAllocator.DEFAULT.buffer();
		buf1.writeBytes(new byte[]{1, 2, 3, 4, 5});
		ByteBuf buf2 = ByteBufAllocator.DEFAULT.buffer();
		buf2.writeBytes(new byte[]{6, 7, 8, 9, 10});
		
		ByteBuf buf3 = ByteBufAllocator.DEFAULT.buffer();
		/*数据复制*/
		buf3.writeBytes(buf1).writeBytes(buf2);
		logBB(buf3);
		buf3.release();
		buf1.resetReaderIndex();
		buf2.resetReaderIndex();
		
		buf1.retain();
		buf2.retain();
		CompositeByteBuf buf4 = ByteBufAllocator.DEFAULT.compositeBuffer();
		/*组合 没有发生数据复制*/
		buf4.addComponents(true, buf1, buf2);
		buf4.setByte(0, 65);
		logBB(buf1);
		
		buf1.release();
		buf2.release();
		logBB(buf4);
		
		buf4.release();
	}
	
	@Test
	public void sliceTest() {
		ByteBuf buf = ByteBufAllocator.DEFAULT.buffer(10);
		buf.writeBytes(new byte[]{'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j'});
		logBB(buf);
		
		/*切片没有发生数据复制 操作相互影响*/
		ByteBuf buf2 = buf.slice(0, 5);
		logBB(buf2);
		ByteBuf buf3 = buf.slice(5, 5);
		logBB(buf3);
		
		buf2.setByte(0, 65);
		logBB(buf2);
		logBB(buf);
		
		/*release 使引用计数减一 引用计数为0时 与之相关的操作均无效 这里使引用计数加1 */
		buf.retain();
		/* buf还没有正在释放 不影响切片*/
		buf.release();
		logBB(buf2);
		
		
		/*不受buf影响 可以retain 再对其操作 最后再释放*/
		buf2.retain();
		buf3.retain();
		
		//buf先释放
		buf.release();
		
		
		buf2.release();
		buf3.release();
		
	}
	
}
