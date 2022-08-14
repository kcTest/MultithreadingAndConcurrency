package com.zkc.netty.util;

import io.netty.buffer.ByteBuf;

import static io.netty.buffer.ByteBufUtil.appendPrettyHexDump;
import static io.netty.util.internal.StringUtil.NEWLINE;

public class ByteBufUtil {
	
	public static void logBB(ByteBuf msg) {
		int length = msg.readableBytes();
		if (length == 0) {
			StringBuilder buf = new StringBuilder(1 + 4);
			buf.append(' ').append("capacity: ").append(msg.capacity()).append("  ")
					.append("readerIndex: ").append(msg.readerIndex()).append("  ")
					.append("writerIndex: ").append(msg.writerIndex());
			System.out.println(buf);
		} else {
			int outputLength = 2 + 10 + 1;
			int rows = length / 16 + (length % 15 == 0 ? 0 : 1) + 4;
			int hexDumpLength = 2 + rows * 80;
			outputLength += hexDumpLength;
			StringBuilder buf = new StringBuilder(outputLength);
			buf.append(' ').append("capacity: ").append(msg.capacity()).append("  ")
					.append("readerIndex: ").append(msg.readerIndex()).append("  ")
					.append("writerIndex: ").append(msg.writerIndex());
			buf.append(NEWLINE);
			appendPrettyHexDump(buf, msg);
			System.out.println(buf);
		}
	}
}
