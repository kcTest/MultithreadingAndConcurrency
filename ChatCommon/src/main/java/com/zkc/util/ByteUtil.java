package com.zkc.util;

public class ByteUtil {
	
	/**
	 * A big-endian system stores the most significant byte of a word at the smallest memory address and the least significant byte at the largest.
	 * A little-endian system, in contrast, stores the least-significant byte at the smallest address
	 * <p>
	 * The difference between big and little endian is the order of the four bytes of the integer being stored.
	 * <p>
	 * int转换为小端byte[]
	 */
	public static int swichBitOrder(int num) {

//		ByteBuffer buffer = ByteBuffer.allocate(4).putInt(num).order(ByteOrder.LITTLE_ENDIAN);
//		buffer.flip();
//		Integer.toHexString(buffer.getInt());
		// abcd -> dcba
		return num >>> 24 | num >> 8 & 0xFF00 | num << 8 & 0xFF0000 | num << 24;
	}
	
	/**
	 * byte转hex
	 */
	public static String byteToHexStr(byte[] byteArr) {
		StringBuilder sb = new StringBuilder();
		for (byte b : byteArr) {
			String hexStr = Integer.toHexString(0xFF & b);
			if (hexStr.length() == 1) {
				sb.append("0");
			}
			sb.append(hexStr);
		}
		return sb.toString();
	}
	
	
	public static String byteToBinaryStr(byte[] byteArr) {
		StringBuilder sb = new StringBuilder();
		for (byte b : byteArr) {
			String binaryStr = Integer.toBinaryString(0xFF & b);
			for (int i = binaryStr.length(); i < 8; i++) {
				sb.append("0");
			}
			sb.append(binaryStr);
		}
		return sb.toString();
	}
}
