package com.zkc.atomic;

import com.zkc.util.Print;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicReference;

/**
 * AtomicReference：引用类型原子类。
 * AtomicMarkableReference：带有更新标记位的原子引用类型。
 * AtomicStampedReference：带有更新版本号的原子引用类型。
 */
public class AtomicReferenceDemo {
	
	@Test
	public void test01() {
		//待包装的User对象
		User zs = new User("1", "zs");
		
		//为原子对象设置值
		AtomicReference<User> userAtomicRef = new AtomicReference<>();
		userAtomicRef.set(zs);
		Print.tco("userAtomicRef is :" + userAtomicRef.get());
		
		//新的User对象
		User ls = new User("2", "ls");
		
		//使用CAS替换zs
		boolean success = userAtomicRef.compareAndSet(zs, ls);
		Print.tco("CAS result is " + success);
		Print.tco("userAtomicRef is :" + userAtomicRef.get());
		/*
		只能保障User引用的原子操作，对被包装的User对象的字段值修改时不能保证原子性
		 */
	}
}
