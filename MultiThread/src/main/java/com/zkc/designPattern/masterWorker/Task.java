package com.zkc.designPattern.masterWorker;

import lombok.Getter;
import lombok.Setter;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

/**
 * 异步任务类在执行子类任务的doExecute()方法之后，回调一下Master传递过来的回调函数，将执行完成后的任务进行回填
 */
public class Task<R> {
	
	private static final AtomicInteger INDEX = new AtomicInteger(1);
	
	/**
	 * 任务的回调函数
	 */
	public Consumer<Task<R>> resultAction;
	/**
	 * 任务的id
	 */
	@Getter
	@Setter
	private int id;
	
	/**
	 * worker的id
	 */
	@Getter
	@Setter
	private int workerId;
	
	/**
	 * 计算结果
	 */
	@Getter
	private R result = null;
	
	public Task() {
		this.id = INDEX.getAndIncrement();
	}
	
	public void execute() {
		this.result = this.doExecute();
		//执行回调函数
		resultAction.accept(this);
	}
	
	/**
	 * 由子类实现
	 */
	protected R doExecute() {
		return null;
	}
}
