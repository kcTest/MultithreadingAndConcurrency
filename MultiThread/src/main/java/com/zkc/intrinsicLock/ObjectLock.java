package com.zkc.intrinsicLock;

import com.zkc.util.ByteUtil;
import com.zkc.util.Print;
import org.openjdk.jol.info.ClassLayout;

public class ObjectLock {
	
	//整型字段占4字节
	private int amount = 0;
	
	public void increase() {
		synchronized (this) {
			amount++;
		}
	}
	
	/**
	 * 输出十六进制
	 */
	public String hexHashStr() {
		int hashCode = this.hashCode();
		//转成十六进制形式的字符串
		return Integer.toHexString(hashCode);
	}
	
	/**
	 * 输出二进制
	 */
	public String binaryHash() {
		//对象原始 hashCode java默认为大端模式
		int hashCode = this.hashCode();
		return Integer.toBinaryString(hashCode);
	}
	
	/**
	 * 输出十六进制字符串
	 */
	public String hexThreadId() {
		//当前线程的threadId 
		return Long.toHexString(Thread.currentThread().getId());
	}
	
	/**
	 * 输出二进制字符串
	 */
	public String binaryThreadId() {
		return Long.toBinaryString(Thread.currentThread().getId());
	}
	
	public void printSelf() {
		//输出十六进制  小端模式的hashCode
		Print.fo(String.format("lock hexHash= %s", hexHashStr()));
		//输出二进制  小端模式的hashCode
		Print.fo(String.format("lock binaryHash= %s", binaryHash()));
		//通过JOL工具获取this的对象布局
		String printable = ClassLayout.parseInstance(this).toPrintable();
		//输出对象布局
		Print.fo(String.format("lock = %s", printable));
	}
	
	public void printObjectStructure() {
		String printable = ClassLayout.parseInstance(this).toPrintable();
		Print.fo(String.format("lock = %s", printable));
	}
	
}
