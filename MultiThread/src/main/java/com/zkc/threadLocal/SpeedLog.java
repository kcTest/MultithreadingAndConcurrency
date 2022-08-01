package com.zkc.threadLocal;


import com.zkc.util.Print;

import java.util.*;

/**
 * 记录函数调用的耗时
 */
public class SpeedLog {
	
	/**
	 * 将保存各个时间点时间的map放入本地变量
	 */
	private static final ThreadLocal<Map<String, Long>> TIME_RECORD_LOCAL = ThreadLocal.withInitial(() -> SpeedLog.initStartTime());
	
	/**
	 * 记录耗时的本地map初始化
	 */
	private static Map<String, Long> initStartTime() {
		Map<String, Long> map = new TreeMap<>();
		map.put("start", System.currentTimeMillis());
		map.put("last", System.currentTimeMillis());
		return map;
	}
	
	/**
	 * 开始前初始化map
	 */
	public static void beginSpeedLog() {
		Print.fo("开始耗时记录");
		//初始化
		TIME_RECORD_LOCAL.get();
	}
	
	/**
	 * 记录
	 */
	public static void logPoint(String point) {
		//获取上一次时间
		Long last = TIME_RECORD_LOCAL.get().get("last");
		//距离上个时间点的时间差 保存
		Long now = System.currentTimeMillis();
		TIME_RECORD_LOCAL.get().put(point + " cost:", now - last);
		//更新当前时间点作为下次记录时的上一时间点
		TIME_RECORD_LOCAL.get().put("last", now);
	}
	
	/**
	 * 结束耗时记录 清理本地变量
	 */
	public static void endSpeedLog() {
		TIME_RECORD_LOCAL.remove();
		Print.fo("结束耗时记录");
	}
	
	public static void printCost() {
		for (Map.Entry<String, Long> entry : TIME_RECORD_LOCAL.get().entrySet()) {
			Print.fo(entry.getKey() + "=> " + entry.getValue());
		}
	}
}
