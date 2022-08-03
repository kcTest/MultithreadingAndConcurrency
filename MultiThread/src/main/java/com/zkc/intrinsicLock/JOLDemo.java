package com.zkc.intrinsicLock;

import com.zkc.util.Print;
import org.junit.Test;
import org.openjdk.jol.vm.VM;

import java.nio.ByteOrder;

public class JOLDemo {
	
	/**
	 * 输出的ObjectLock对象为16字节，其中对象头（Object Header）占12字节，剩下的4字节由amount
	 * 属性（字段）占用。由于16字节为8字节的倍数，因此没有对齐填充字节（JVM规定对象头部分必须是8字节的倍数，否则需要对齐填充）
	 */
	@Test
	public void showNoLockObject() {
		//查看JVM使用的字节序
		Print.fo(ByteOrder.nativeOrder());
		//输出JVM的信息
		Print.fo(VM.current().details());
		//创建对象  分析该类的实例对象
		ObjectLock objectLock = new ObjectLock();
		Print.fo("object status: ");
		//输出对象布局
		objectLock.printSelf();
	}
	
}
