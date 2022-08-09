package com.zkc.util;

import sun.misc.Unsafe;

import java.lang.management.ManagementFactory;
import java.lang.reflect.Field;

public class JvmUtil {
	
	public static String getProcessId() {
		return ManagementFactory.getRuntimeMXBean().getName().split("@")[0];
	}
	
	public static Unsafe getUnsafe() {
		try {
			Field theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
			theUnsafe.setAccessible(true);
			return (Unsafe) theUnsafe.get(null);
		} catch (Exception e) {
			throw new AssertionError(e);
		}
	}
}
