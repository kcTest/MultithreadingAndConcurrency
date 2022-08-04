package com.zkc.atomic;

import com.zkc.util.Print;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicIntegerArray;

/**
 * AtomicIntegerArray：整型数组原子类。
 * AtomicLongArray：长整型数组原子类。
 * AtomicReferenceArray：引用类型数组原子类
 */
public class AtomicArrayDemo {
	
	@Test
	public void test01() {
		int[] arr = new int[]{1, 2, 3, 4, 5, 6};
		//包装为原子数组
		AtomicIntegerArray i = new AtomicIntegerArray(arr);
		
		//获取第0个元素 然后设置为2 [AtomicArrayDemo:test01]: tempVal=1; i=[2, 2, 3, 4, 5, 6]
		int tempVal = i.getAndSet(0, 2);
		Print.fo("tempVal=" + tempVal + "; i=" + i);
		
		//获取第0个元素 然后自增 [AtomicArrayDemo:test01]: tempVal=2; i=[3, 2, 3, 4, 5, 6]
		tempVal = i.getAndIncrement(0);
		Print.fo("tempVal=" + tempVal + "; i=" + i);
		
		//获取第0个元素 然后自增 [AtomicArrayDemo:test01]: tempVal=3; i=[8, 2, 3, 4, 5, 6]
		tempVal = i.getAndAdd(0, 5);
		Print.fo("tempVal=" + tempVal + "; i=" + i);
	}
}
