package com.zkc.nio.selector.multiThread;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;

import static com.zkc.nio.util.ByteBufferUtil.debugAll;

/**
 * 非阻塞模式 selector 多线程处理 多路复用
 * 一个boss线程+bossSelector处理多个客户端连接
 * 单个worker线程+workerSelector处理多个客户端读写
 * <p>
 * 处理select()和sc.register执行顺序问题
 */
public class ServerDemo08 {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ServerDemo08.class);
	
	public static void main(String[] args) throws IOException {
		Thread.currentThread().setName("BOSS");
		
		//boss只负责建立连接 
		Selector bossSelector = Selector.open();
		
		ServerSocketChannel ssc = ServerSocketChannel.open();
		ssc.configureBlocking(false);
		ssc.bind(new InetSocketAddress(8080));
		ssc.register(bossSelector, SelectionKey.OP_ACCEPT);
		
		//防止每次连接都创建一个新的worker 拿到外层 创建固定数量的worker
		Worker worker = new Worker("worker-0");
		//worker.register();
		
		while (true) {
			bossSelector.select();
			Iterator<SelectionKey> iterator = bossSelector.selectedKeys().iterator();
			while (iterator.hasNext()) {
				SelectionKey curKey = iterator.next();
				if (curKey.isAcceptable()) {
					SocketChannel sc = ssc.accept();
					sc.configureBlocking(false);
					LOGGER.debug("connected: " + sc.getRemoteAddress());
					LOGGER.debug("before register: " + sc.getRemoteAddress());
					//关联sc和selector 静态内部类直接访问私有selector
					//sc.register(worker.selector, SelectionKey.OP_READ);
					/*
					worker.register()已经调用,selector在worker的run方法中调用selector.select()被阻塞。
					 此处关联sc再次使用worker的selector也会阻塞住 无法向下运行
					 需要保持执行顺序 
					 在此处调用worker.register()将sc放入worker 在worker线程中去处理关联动作
					 */
					worker.register(sc);
					LOGGER.debug("after register: " + sc.getRemoteAddress());
				}
				iterator.remove();
			}
		}
	}
	
	/**
	 * worker负责读写
	 */
	private static class Worker implements Runnable {
		private Thread thread;
		private Selector selector;
		private String name;
		private volatile boolean start = false;
		private ConcurrentLinkedQueue<Runnable> queue = new ConcurrentLinkedQueue<>();
		
		public Worker(String name) {
			this.name = name;
		}
		
		/**
		 * 只执行一次 用一个线程和选择器负责多个客户端读写
		 *
		 * @param sc
		 */
		public void register(SocketChannel sc) throws IOException {
			if (!start) {
				selector = Selector.open();
				thread = new Thread(this, name);
				thread.start();
				start = true;
			}
			//（此处调用方还是boss线程）先加入线程安全的任务队列去传递数据到worker线程 在worker线程中去执行
			queue.add(() -> {
				try {
					sc.register(selector, SelectionKey.OP_READ);
				} catch (ClosedChannelException e) {
					e.printStackTrace();
				}
			});
			//唤醒select  在worker线程执行sc注册
			selector.wakeup();
			
			/*wakeup调用后下次的select也会立即返回 除非调用的是selectNow
			也可以使用下面代码解决顺序问题
			selector.wakeUp();
			sc.register(selector, SelectionKey.OP_READ);
			*/
		}
		
		@Override
		public void run() {
			while (true) {
				try {
					selector.select();
					//被唤醒或者有事件触发时 检查任务队列取出注册任务去执行
					Runnable task = queue.poll();
					if (task != null) {
						task.run();
					}
					Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
					while (iterator.hasNext()) {
						SelectionKey curKey = iterator.next();
						if (curKey.isReadable()) {
							ByteBuffer buffer = ByteBuffer.allocate(16);
							SocketChannel sc = (SocketChannel) curKey.channel();
							LOGGER.debug("read: " + sc.getRemoteAddress());
							sc.read(buffer);
							buffer.flip();
							debugAll(buffer);
							/*先不考虑边界等问题*/
						}
						iterator.remove();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
	}
}
