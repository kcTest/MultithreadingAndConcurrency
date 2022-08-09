package com.zkc.util;

import java.util.concurrent.ThreadLocalRandom;

public class RandomUtil {
	
	/**
	 * 生成位于[1,abs(bound)]范围上的随机数
	 */
	public static int random(int bound) {
		return ThreadLocalRandom.current().nextInt(bound) + 1;
	}
	
	/**
	 * 生成位于[low,high]范围上的随机数
	 */
	public static float randomInRange(int low, int high) {
		return random(high - low) + low;
	}
}
