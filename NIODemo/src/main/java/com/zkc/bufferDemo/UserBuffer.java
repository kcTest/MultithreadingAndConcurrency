package com.zkc.bufferDemo;

import com.zkc.util.Logger;

import java.nio.IntBuffer;

/**
 * Java New IO类库，简称为Java NIO 包含以下三个核心组件：
 * Channel（通道）
 * Buffer（缓冲区）
 * Selector（选择器）
 * <p>
 * Buffer类的几个常用方法，包含Buffer实例的创建、写入、读取、重复读、标记和重置等。
 * <p>
 * （1）使用创建子类实例对象的allocate()方法创建一个Buffer类的实例对象。
 * （2）调用put()方法将数据写入缓冲区中。
 * （3）写入完成后，在开始读取数据前调用Buffer.flip()方法，将缓冲区转换为读模式。
 * （4）调用get()方法，可以从缓冲区中读取数据。
 * （5）读取完成后，调用Buffer.clear()方法或Buffer.compact()方法，将缓冲区转换为写模式，可以继续写入
 */
public class UserBuffer {
	
	private static IntBuffer intBuffer;
	
	public static void main(String[] args) {
		Logger.debug("分配内存");
		allocateTest();
		
		Logger.debug("写入");
		putTest();
		
		Logger.debug("翻转");
		flipTest();
		
		Logger.debug("读取");
		getTest();
		
		Logger.debug("重复读");
		rewindTest();
		getAndMark();
		
		Logger.debug("标记&重置之后读取");
		resetTest();
		
		Logger.debug("清空");
		clearTest();
	}
	
	/**
	 * 在读模式下，调用clear()方法将缓冲区切换为写模式。此方法的作用是：
	 * （1）将position清零。
	 * （2）limit设置为capacity最大容量值，可以一直写入，直到缓冲区写满。
	 */
	private static void clearTest() {
		intBuffer.clear();
		Logger.debug("------------after clear------------------");
		Logger.debug("position=" + intBuffer.position());
		Logger.debug("limit=" + intBuffer.limit());
		Logger.debug("capacity=" + intBuffer.capacity());
	}
	
	/**
	 * 首先调用reset()把mark中的值恢复到position中，因此读取的位置position就是2，表示可以再次开始从第三个元素开始读取数据
	 */
	private static void resetTest() {
		intBuffer.reset();
		Logger.debug("------------after reset------------------");
		Logger.debug("position=" + intBuffer.position());
		Logger.debug("limit=" + intBuffer.limit());
		Logger.debug("capacity=" + intBuffer.capacity());
		while (intBuffer.position() != intBuffer.limit()) {
			int j = intBuffer.get();
			Logger.debug("j = " + j);
		}
		Logger.debug("------------after get------------------");
		Logger.debug("position=" + intBuffer.position());
		Logger.debug("limit=" + intBuffer.limit());
		Logger.debug("capacity=" + intBuffer.capacity());
	}
	
	
	/**
	 * 与读取代码基本相同，只是增加了一个mark调用。
	 * Buffer.mark()方法将当前position的值保存起来放在mark属性中，让mark属性记住这个临时位置；
	 * 然后可以调用Buffer.reset()方法将mark的值恢复到position中。
	 */
	private static void getAndMark() {
		for (int i = 0; i < 5; i++) {
			if (i == 2) {
				intBuffer.mark();
			}
			int j = intBuffer.get();
			Logger.debug("j = " + j);
		}
		Logger.debug("------------after getAndMark------------------");
		Logger.debug("position=" + intBuffer.position());
		Logger.debug("limit=" + intBuffer.limit());
		Logger.debug("capacity=" + intBuffer.capacity());
	}
	
	/**
	 * 已经读完的数据，如果需要再读一遍，可以先调用rewind()方法。
	 * (既可以通过倒带方法rewind()去完成，也可以通过mark()和reset()两个方法组合实现。)
	 * <p>
	 * rewind()方法主要是调整了缓冲区的position属性与mark属性，
	 * 具体的调整规则如下：
	 * （1）position重置为0，所以可以重读缓冲区中的所有数据。
	 * （2）limit保持不变，数据量还是一样的，仍然表示能从缓冲区中读取的元素数量。
	 * （3）mark被清理，表示之前的临时位置不能再用了
	 */
	private static void rewindTest() {
		intBuffer.rewind();
		Logger.debug("------------after rewind------------------");
		Logger.debug("position=" + intBuffer.position());
		Logger.debug("limit=" + intBuffer.limit());
		Logger.debug("capacity=" + intBuffer.capacity());
	}
	
	/**
	 * 调用get()方法每次从position的位置读取一个数据，并且进行相应的缓冲区属性的调整
	 * <p>
	 * 读取操作会改变可读位置position的属性值，而可读上限limit值并不会改变。
	 * 在position值和limit值相等时，表示所有数据读取完成，position指向了一个没有数据的元素位置，已经不能再读了
	 */
	private static void getTest() {
		for (int i = 0; i < 2; i++) {
			int j = intBuffer.get();
			Logger.debug("j = " + j);
		}
		Logger.debug("------------after get 2 int------------------");
		Logger.debug("position=" + intBuffer.position());
		Logger.debug("limit=" + intBuffer.limit());
		Logger.debug("capacity=" + intBuffer.capacity());
		
		for (int i = 0; i < 3; i++) {
			int j = intBuffer.get();
			Logger.debug("j = " + j);
		}
		Logger.debug("------------after get 3 int------------------");
		Logger.debug("position=" + intBuffer.position());
		Logger.debug("limit=" + intBuffer.limit());
		Logger.debug("capacity=" + intBuffer.capacity());
	}
	
	/**
	 * 向缓冲区写入数据之后，是否可以直接从缓冲区读取数据呢？不能！
	 * 这时缓冲区还处于写模式，如果需要读取数据，要将缓冲区转换成读模式。
	 * flip()翻转方法是Buffer类提供的一个模式转变的重要方法，作用是将写模式翻转成读模式
	 * <p>
	 * 首先，设置可读上限limit的属性值。将写模式下的缓冲区中内容
	 * 的最后写入位置position值作为读模式下的limit上限值。
	 * 其次，把读的起始位置position的值设为0，表示从头开始读。
	 * 最后，清除之前的mark标记，因为mark保存的是写模式下的临时
	 * 位置，发生模式翻转后，如果继续使用旧的mark标记，就会造成位置混乱
	 */
	private static void flipTest() {
		intBuffer.flip();
		Logger.debug("------------after flip------------------");
		Logger.debug("position=" + intBuffer.position());
		Logger.debug("limit=" + intBuffer.limit());
		Logger.debug("capacity=" + intBuffer.capacity());
	}
	
	/**
	 * 调用allocate()方法分配内存、返回了实例对象后，缓冲区实
	 * 例对象处于写模式，可以写入对象，如果要把对象写入缓冲区，就需
	 * 要调用put()方法,要求写入的数据类型与缓冲区的类型保持一致。
	 * <p>
	 * 写入了5个元素之后，缓冲区的position属性值
	 * 变成了5，所以指向了第6个（从0开始的）可以进行写入的元素位置。
	 * limit最大可写上限、capacity最大容量两个属性的值都没有发生变
	 * 化
	 */
	private static void putTest() {
		for (int i = 0; i < 5; i++) {
			intBuffer.put(i);
		}
		
		Logger.debug("------------after put------------------");
		Logger.debug("position=" + intBuffer.position());
		Logger.debug("limit=" + intBuffer.limit());
		Logger.debug("capacity=" + intBuffer.capacity());
	}
	
	/**
	 * 获取一个Buffer实例对象时，并不是使
	 * 用子类的构造器来创建，而是调用子类的allocate()方法
	 * <p>
	 * 一个缓冲区在新建后处于写模式，
	 * position属性（代表写入位置）的值为0，缓冲区的capacity值是初始
	 * 化时allocate方法的参数值（这里是20），而limit最大可写上限值也
	 * 为allocate方法的初始化参数值
	 */
	private static void allocateTest() {
		intBuffer = IntBuffer.allocate(20);
		Logger.debug("------------after allocate------------------");
		Logger.debug("position=" + intBuffer.position());
		Logger.debug("limit=" + intBuffer.limit());
		Logger.debug("capacity=" + intBuffer.capacity());
	}
	
}
