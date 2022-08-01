package com.zkc.threadLocal;

import com.zkc.util.Print;
import com.zkc.util.ThreadUtil;
import lombok.Data;

import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;

public class ThreadLocalDemo01 {
	
	public static void main(String[] args) {
		//获取自定义的混合型线程池
		ThreadPoolExecutor pool = ThreadUtil.getMixedTargetThreadPool();
		//提交五个任务 使用5个线程
		for (int i = 0; i < MAX; i++) {
			pool.execute(new Runnable() {
				@Override
				public void run() {
					//获取 线程本地变量 中当前线程所绑定的值
//					if (LOCAL_FOO.get() == null) {
//						LOCAL_FOO.set(new Foo());
//					}
					Print.tco("初始的本地值:" + LOCAL_FOO.get());
					//每个线程执行10次
					for (int j = 0; j < 10; j++) {
						Foo foo = LOCAL_FOO.get();
						//每次增1
						foo.setBar(foo.getBar() + 1);
					}
					Print.tco("累计10次之后的本地值：" + LOCAL_FOO.get());
					//删除 线程本地变量 中当前线程绑定的值
					LOCAL_FOO.remove();
				}
			});
		}
	}
	
	private static final int MAX = 5;
//	private static final ThreadLocal<Foo> LOCAL_FOO = new ThreadLocal<>();
	/**
	 * 也可以调用ThreadLocal.withInitial(…)静态工厂方法，在定义ThreadLocal对象时设置一个获取初始值的回调函数
	 */
	private static final ThreadLocal<Foo> LOCAL_FOO = ThreadLocal.withInitial(() -> new Foo());
	
	@Data
	private static class Foo {
		/**
		 * 实例总数
		 */
		private static final AtomicInteger SEQ = new AtomicInteger(0);
		/**
		 * 对象编号
		 */
		int NO;
		/**
		 * 对象的内容
		 */
		int bar = 3;
		
		public Foo() {
			/*给对象编号*/
			NO = SEQ.incrementAndGet();
		}
		
		@Override
		public String toString() {
			return String.format("%d@Foo{bar=%d}", NO, bar);
		}
	}
}
