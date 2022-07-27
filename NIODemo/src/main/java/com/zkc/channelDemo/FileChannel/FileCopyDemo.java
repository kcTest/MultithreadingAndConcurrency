package com.zkc.channelDemo.FileChannel;

import com.zkc.NIODemoConfig;
import com.zkc.util.IOUtil;
import com.zkc.util.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * FileChannel（文件通道）是专门操作文件的通道。通过FileChannel，既可以从一个文件中读取数据，也可以将数据写入文件中。
 * FileChannel为阻塞模式，不能设置为非阻塞模式。
 * FileChannel并没有继承SelectableChannel，因此不是可选择通道。
 * FileChannel不能与选择器一起使用。
 */
public class FileCopyDemo {
	
	public static void main(String[] args) {
		copyResourceFile();
	}
	
	/**
	 *
	 */
	private static void copyResourceFile() {
		String srcFileName = NIODemoConfig.SRC_FILENAME_COPY;
		String srcPath = IOUtil.getResourcePath(srcFileName);
		Logger.debug("srcPath=" + srcPath);
		
		String destFileName = NIODemoConfig.DEST_FILENAME_COPY;
		String destPath = IOUtil.buildResourcePath(destFileName);
		Logger.debug("destPath=" + destPath);
		
		nioCopyFile(srcPath, destPath);
	}
	
	private static void nioCopyFile(String srcFullPath, String destFullPath) {
		File srcFile = new File(srcFullPath);
		File destFile = new File(destFullPath);
		try {
			if (!destFile.exists()) {
				destFile.createNewFile();
			}
			long startTime = System.currentTimeMillis();
			
			FileInputStream fis = null;
			FileChannel ic = null;
			FileOutputStream fos = null;
			FileChannel oc = null;
			
			try {
				fis = new FileInputStream(srcFile);
				ic = fis.getChannel();
				fos = new FileOutputStream(destFile);
				oc = fos.getChannel();
				//先从输入通道读取数据写入到buf 
				int loop = 0, maxSize = 1024, rest = 0;
				ByteBuffer buf = ByteBuffer.allocate(maxSize);
				while (ic.read(buf) != -1) {
					//先翻转 改为读模式
					buf.flip();
					//再从buf中将数据取出写入到输出通道
					//记录读取字节数
					while ((rest = oc.write(buf)) != 0) {
						loop++;
					}
					//清空buf 改为写入模式 继续读取输入通道剩余数据
					buf.clear();
				}
				Logger.debug("写入字节数:" + (loop * maxSize + rest));
			/*
			在将缓冲区数据写入通道时，要保证数据能写入磁盘，可以在写入后调用一下FileChannel的force()方法。
			*/
				oc.force(true);
			} finally {
				IOUtil.closeQuietly(oc);
				IOUtil.closeQuietly(fos);
				IOUtil.closeQuietly(ic);
				IOUtil.closeQuietly(fis);
			}
			long endTime = System.currentTimeMillis();
			Logger.debug("复制毫秒数: " + (endTime - startTime));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
