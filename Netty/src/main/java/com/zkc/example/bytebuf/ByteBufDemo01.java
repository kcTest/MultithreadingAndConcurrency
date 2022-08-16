package com.zkc.example.bytebuf;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;

import static com.zkc.example.util.BufUtil.logBB;

@Slf4j
public class ByteBufDemo01 {
	
	public static void main(String[] args) {
		/*默认直接内存 池化  可以通过VM选项修改-Dio.netty.allocator.type=unpooled 修改后由GC回收 正常在池中重复利用或回收*/
		ByteBuf buf = ByteBufAllocator.DEFAULT.buffer();
//		ByteBuf buf = ByteBufAllocator.DEFAULT.directBuffer();
//		ByteBuf buf = ByteBufAllocator.DEFAULT.heapBuffer();
		/*capacity 默认256 最大Integer.MAX_VALUE*/
		logBB(buf);
		
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 6; i++) {
			sb.append("a");
		}
		buf.writeBytes(sb.toString().getBytes(StandardCharsets.UTF_8));
		/*512*/
		logBB(buf);
		
		/*1、【 [当前容量为16的倍数？当前容量：大于当前容量的下一个16的倍数]-[已写数量]>[单次写入数量（int=4）] 】? 【扩容为大于当前容量的下一个16的倍数】：【从64开始倍增直到大于等于（大于等于当前容量的下一个16的倍数）】 */
		/*2、【如果新容量<=4MiB】？【新容量】:【Min[(新容量/4MiB+1)*4MiB,MAX]】 */
		ByteBuf buf2 = ByteBufAllocator.DEFAULT.directBuffer(10);//19  10
		for (int i = 0; i < 5; i++) {
			buf2.writeInt(i);
			logBB(buf2);
		}
		buf2.markReaderIndex();
		System.out.println(buf2.readInt());
		System.out.println(buf2.readInt());
		//从标记位置重复读
		buf2.resetReaderIndex();
		System.out.println(buf2.readInt());
		System.out.println(buf2.readInt());
		System.out.println(buf2.readInt());
		//按索引获取
		System.out.println(buf2.getInt(0));
		System.out.println(buf2.readInt());
	}
	
}
