package com.zkc.asyncCallback;

import com.zkc.util.Print;
import com.zkc.util.ThreadUtil;
import org.junit.Test;

import java.util.concurrent.*;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;


public class CompletionFutureDemo {
	
	/**
	 * 创建一个无输入值、无返回值的异步子任务
	 */
	@Test
	public void runAsyncDemon() throws Exception {
		CompletableFuture<Void> completableFuture = CompletableFuture.runAsync(() -> {
			//模拟执行1s
			ThreadUtil.sleepSeconds(1);
			Print.tcfo("run end......");
		});
		
		//等待异步子任务执行完成 限时等待2s
		completableFuture.get(2, TimeUnit.SECONDS);
	}
	
	/**
	 * 创建一个无输入值、有返回值的异步子任务
	 */
	@Test
	public void supplyAsyncDemon() throws Exception {
		CompletableFuture<Long> completableFuture = CompletableFuture.supplyAsync(() -> {
			long start = System.currentTimeMillis();
			//模拟执行1s
			ThreadUtil.sleepSeconds(1);
			Print.tcfo("run end......");
			return System.currentTimeMillis() - start;
		});
		
		//等待异步子任务执行完成 限时等待2s
		Long time = completableFuture.get(2, TimeUnit.SECONDS);
		Print.tcfo("异步执行耗时结果为:" + time / 1000 + "s");
	}
	
	/**
	 * 为CompletionStage子任务设置完成钩子和异常钩子
	 */
	@Test
	public void whenCompleteDemo() throws ExecutionException, InterruptedException {
		//创建异步任务
		CompletableFuture<Void> completableFuture = CompletableFuture.runAsync(() -> {
			ThreadUtil.sleepSeconds(1);
			Print.tco("抛出异常");
			throw new RuntimeException("发生异常");
		});
		
		//设置异步任务执行完成后的回调钩子
		completableFuture.whenComplete(new BiConsumer<Void, Throwable>() {
			@Override
			public void accept(Void unused, Throwable throwable) {
				Print.tco("执行完成");
			}
		});
		
		//设置异步任务执行发生异常后的回调钩子
		completableFuture.exceptionally(new Function<Throwable, Void>() {
			@Override
			public Void apply(Throwable throwable) {
				Print.tco("执行失败 " + throwable.getMessage());
				return null;
			}
		});
		
		//获取异步任务的结果
		completableFuture.get();
	}
	
	/**
	 * 调用handle()方法统一处理异常和结果
	 */
	@Test
	public void handleDemo() throws ExecutionException, InterruptedException {
		CompletableFuture<Void> completableFuture = CompletableFuture.runAsync(() -> {
			ThreadUtil.sleepSeconds(1);
			Print.tco("抛出异常");
			throw new RuntimeException("发生异常");
		});
		
		//统一处理异常和结果
		completableFuture.handle(new BiFunction<Void, Throwable, Void>() {
			@Override
			public Void apply(Void unused, Throwable throwable) {
				
				if (throwable == null) {
					Print.tco("没有发生异常");
				} else {
					Print.tco("发生了异常");
				}
				return null;
			}
		});
		
		completableFuture.get();
	}
	
	/**
	 * 默认情况下，通过静态方法runAsync()、supplyAsync()创建的CompletableFuture任务会使用公共的ForkJoinPool线程池，
	 * 默认的线程数是CPU的核数
	 * <p>
	 * 指定线程池
	 * 使用混合型任务线程池执行CompletableFuture任务
	 */
	@Test
	public void threadPoolDemo() throws ExecutionException, InterruptedException, TimeoutException {
		ThreadPoolExecutor pool = ThreadUtil.getMixedTargetThreadPool();
		CompletableFuture<Long> completableFuture = CompletableFuture.supplyAsync(() -> {
			Print.tco("run begin...");
			long start = System.currentTimeMillis();
			ThreadUtil.sleepSeconds(1);
			Print.tco("run end...");
			return System.currentTimeMillis() - start;
		}, pool);
		
		//等待异步任务执行完成 限时等待2s
		long time = completableFuture.get(2, TimeUnit.SECONDS);
		Print.tco("异步执行耗时：" + time / 1000 + "s");
	}
	
	/**
	 * 异步任务的串行执行
	 * 调用thenApply()分两步计算（10+10）*2
	 * <p>
	 */
	@Test
	public void thenApplyDemo() throws ExecutionException, InterruptedException {
		CompletableFuture<Long> completableFuture = CompletableFuture.supplyAsync(new Supplier<Long>() {
			@Override
			public Long get() {
				long firstOutcome = 10L + 10L;
				Print.tco("firstOutcome is " + firstOutcome);
				return firstOutcome;
			}
		}).thenApplyAsync(new Function<Long, Long>() {//T R
			/*
			 *thenApply可以将前一个任务的结果通过Function的 [R apply(T t)]方法传递到第二个任务，并且能输出第二个任务的执行结果
			 */
			@Override
			public Long apply(Long firstOutcome) {
				long secondOutcome = firstOutcome << 1;
				Print.tco("secondOutcome is " + secondOutcome);
				return secondOutcome;
			}
		});
		long result = completableFuture.get();
		Print.tco(" outcome is " + result);
	}
	
	/**
	 * thenCompose()方法要求第二个任务的返回值是一个CompletionStage异步实例。
	 * 可以调用CompletableFuture.supplyAsync()方法将第二个任务所要调用的普通异步方法包装成一个CompletionStage异步实例
	 * <p>
	 * 调用thenCompose分两步计算（10+10）*2
	 */
	@Test
	public void thenComposeDemo() throws ExecutionException, InterruptedException {
		CompletableFuture<Long> completableFuture = CompletableFuture.supplyAsync(new Supplier<Long>() {
			@Override
			public Long get() {
				long firstOutcome = 10L + 10L;
				Print.tco("firstOutcome is " + firstOutcome);
				return firstOutcome;
			}
		}).thenCompose(new Function<Long, CompletionStage<Long>>() {
			@Override
			public CompletionStage<Long> apply(Long firstOutcome) {
				//将第二个任务要调用的普通异步方法包装成一个CompletableStage异步实例返回
				return CompletableFuture.supplyAsync(new Supplier<Long>() {
					//要调用的普通异步方法
					@Override
					public Long get() {
						long secondOutcome = firstOutcome;
						Print.tco("secondOutcome is " + secondOutcome);
						return secondOutcome;
					}
				});
			}
		});
		
		long result = completableFuture.get();
		Print.tco("outcome is " + result);
	}
	
	/**
	 * 调用thenCombine分三步计算（10+10）*（10+10）
	 * thenCombine()方法将第一步、第二步的结果合并到第三步上
	 */
	@Test
	public void thenCombineDemo() throws ExecutionException, InterruptedException {
		CompletableFuture<Long> future1 = CompletableFuture.supplyAsync(new Supplier<Long>() {
			@Override
			public Long get() {
				long firstOutcome = 10L + 10L;
				Print.tco("firstOutcome is " + firstOutcome);
				return firstOutcome;
			}
		});
		CompletableFuture<Long> future2 = CompletableFuture.supplyAsync(new Supplier<Long>() {
			@Override
			public Long get() {
				long secondOutcome = 10L + 10L;
				Print.tco("secondOutcome is " + secondOutcome);
				return secondOutcome;
			}
		});
		CompletableFuture<Long> future3 = future1.thenCombine(future2, new BiFunction<Long, Long, Long>() {
			@Override
			public Long apply(Long firstOutcome, Long secondOutcome) {
				return firstOutcome + secondOutcome;
			}
		});
		
		Long result = future3.get();
		Print.tco(" outcome is " + result);
	}
	
	/**
	 * thenCombine()只能合并两个任务，如果需要合并多个异步任务，那么可以调用allOf()
	 */
	@Test
	public void allOfDemo() {
		CompletableFuture<Void> future1 = CompletableFuture.runAsync(() -> {
			Print.tco("模拟异步任务1");
		});
		CompletableFuture<Void> future2 = CompletableFuture.runAsync(() -> {
			Print.tco("模拟异步任务2");
		});
		CompletableFuture<Void> future3 = CompletableFuture.runAsync(() -> {
			Print.tco("模拟异步任务3");
		});
		CompletableFuture<Void> future4 = CompletableFuture.runAsync(() -> {
			Print.tco("模拟异步任务4");
		});
		
		CompletableFuture<Void> allFuture = CompletableFuture.allOf(future1, future2, future3, future4);
		allFuture.join();
		Print.tco("运行结束");
	}
	
	/**
	 * 两个CompletionStage谁返回结果的速度快，applyToEither()方法就用这个最快的CompletionStage的结果进行下一步（第三步）的回调操作
	 * <p>
	 * 调用applyToEither随机选择（10+10）和（100+100）的结果
	 */
	@Test
	public void applyToEitherDemo() throws ExecutionException, InterruptedException {
		CompletableFuture<Long> future1 = CompletableFuture.supplyAsync(new Supplier<Long>() {
			@Override
			public Long get() {
				long firstOutcome = 100L + 100L;
				Print.tco("firstOutcome is " + firstOutcome);
				return firstOutcome;
			}
		});
		CompletableFuture<Long> future2 = CompletableFuture.supplyAsync(new Supplier<Long>() {
			@Override
			public Long get() {
				long secondOutcome = 10L + 10L;
				Print.tco("secondOutcome is " + secondOutcome);
				return secondOutcome;
			}
		});
		
		CompletableFuture<Long> future3 = future1.applyToEither(future2, new Function<Long, Long>() {
			@Override
			public Long apply(Long aLong) {
				return aLong;
			}
		});
		
		Long result = future3.get();
		Print.tco("outcome is " + result);
	}
	
	/**
	 * 模拟RPC调用1
	 */
	public String rpc1() {
		//睡眠400ms 模拟执行耗时
		ThreadUtil.sleepMilliseconds(500);
		Print.tcfo("模拟RPC调用，服务器server 1");
		return "resp from server1";
	}
	
	/**
	 * 模拟RPC调用2
	 */
	public String rpc2() {
		//睡眠400ms 模拟执行耗时
		ThreadUtil.sleepMilliseconds(500);
		Print.tcfo("模拟RPC调用，服务器server 2");
		return "resp from server2";
	}
	
	/**
	 * 使用CompletableFuture进行多个RPC调用
	 */
	@Test
	public void rpcDemo() throws ExecutionException, InterruptedException {
		CompletableFuture<String> future1 = CompletableFuture.supplyAsync(() -> rpc1());
		CompletableFuture<String> future2 = CompletableFuture.supplyAsync(() -> rpc2());
		CompletableFuture<String> future3 = future1.thenCombine(future2, new BiFunction<String, String, String>() {
			@Override
			public String apply(String s, String s2) {
				return s + "&" + s2;
			}
		});
		Print.tco("最终合并结果为:" + future3.get());
	}
}
