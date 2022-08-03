package com.zkc.util;

import java.lang.management.ManagementFactory;

public class JvmUtil {
	
	public static String getProcessId() {
		return ManagementFactory.getRuntimeMXBean().getName().split("@")[0];
	}
}
