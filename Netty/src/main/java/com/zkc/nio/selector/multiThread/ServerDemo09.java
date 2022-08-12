package com.zkc.nio.selector.multiThread;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

import static com.zkc.nio.util.ByteBufferUtil.debugAll;

/**
 * 非阻塞模式 selector 多线程处理 多路复用
 * 一个boss线程+bossSelector处理多个客户端连接
 * 多个worker线程+workerSelector处理多个客户端读写
 * <p>
 * 平均分配客户端给worker
 */
public class ServerDemo09 {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ServerDemo09.class);
	
	public static void main(String[] args) throws IOException {
		Thread.currentThread().setName("BOSS");
		
		Selector bossSelector = Selector.open();
		
		ServerSocketChannel ssc = ServerSocketChannel.open();
		ssc.configureBlocking(false);
		ssc.bind(new InetSocketAddress(8080));
		ssc.register(bossSelector, SelectionKey.OP_ACCEPT);
		
		//创建worker数组 为充分利用多核cpu线程数据也可以设置为CPU的数量
		Worker[] workers = new Worker[2];
		for (int i = 0; i < workers.length; i++) {
			workers[i] = new Worker("worker-" + i);
		}
		//计数器 分配客户端使用
		AtomicInteger index = new AtomicInteger();
		
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
					//随机分配
					workers[index.getAndIncrement() % workers.length].register(sc);
					LOGGER.debug("after register: " + sc.getRemoteAddress());
				}
				iterator.remove();
			}
		}
	}
	
	private static class Worker implements Runnable {
		private Thread thread;
		private Selector selector;
		private String name;
		private volatile boolean start = false;
		private ConcurrentLinkedQueue<Runnable> queue = new ConcurrentLinkedQueue<>();
		
		public Worker(String name) {
			this.name = name;
		}
		
		public void register(SocketChannel sc) throws IOException {
			if (!start) {
				selector = Selector.open();
				thread = new Thread(this, name);
				thread.start();
				start = true;
			}
			queue.add(() -> {
				try {
					sc.register(selector, SelectionKey.OP_READ);
				} catch (ClosedChannelException e) {
					e.printStackTrace();
				}
			});
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
