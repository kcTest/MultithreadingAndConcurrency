package com.zkc.atomic;

import com.zkc.util.Print;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

/**
 * AtomicIntegerFieldUpdater：原子更新整型字段的更新器。
 * AtomicLongFieldUpdater：原子更新长整型字段的更新器。
 * AtomicReferenceFieldUpdater：原子更新引用类型字段的更新器
 */
public class AtomicFieldUpdaterDemo {
	
	@Test
	public void test01() {
		//调用静态方法newUpdater()创建一个更新器
		AtomicIntegerFieldUpdater<User> fieldUpdater = AtomicIntegerFieldUpdater.newUpdater(User.class, "age");
		
		User zs = new User("1", "zs");
		
		//增加User的age值 0->1->101
		Print.tco(fieldUpdater.getAndIncrement(zs));
		Print.tco(fieldUpdater.getAndAdd(zs, 100));
		
		//获取Uer的age值
		Print.tco(fieldUpdater.get(zs));
	}
}
