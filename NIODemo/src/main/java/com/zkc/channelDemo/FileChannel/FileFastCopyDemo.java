package com.zkc.channelDemo.FileChannel;

import com.zkc.NIODemoConfig;
import com.zkc.util.IOUtil;
import com.zkc.util.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

/**
 * 更高效的文件复制可以调用文件通道的transferFrom()方法
 */
public class FileFastCopyDemo {
	
	public static void main(String[] args) {
		fastCopyResourceFile();
	}
	
	/**
	 *
	 */
	private static void fastCopyResourceFile() {
		String srcFileName = NIODemoConfig.SRC_FILENAME_COPY;
		String srcPath = IOUtil.getResourcePath(srcFileName);
		Logger.debug("srcPath=" + srcPath);
		
		String destFileName = NIODemoConfig.DEST_FILENAME_COPY;
		String destPath = IOUtil.buildResourcePath(destFileName);
		Logger.debug("destPath=" + destPath);
		
		nioFastCopyFile(srcPath, destPath);
	}
	
	private static void nioFastCopyFile(String srcFullPath, String destFullPath) {
		File srcFile = new File(srcFullPath);
		File destFile = new File(destFullPath);
		try {
			if (!destFile.exists()) {
				destFile.createNewFile();
			}
			long startTime = System.currentTimeMillis();
			
			FileInputStream fis = null;
			FileOutputStream fos = null;
			FileChannel ic = null, oc = null;
			
			try {
				fis = new FileInputStream(srcFile);
				ic = fis.getChannel();
				fos = new FileOutputStream(destFile);
				oc = fos.getChannel();
				long total = ic.size(), pos = 0;
				while (pos < total) {
					pos += oc.transferFrom(ic, pos, 1024);
				}
				Logger.debug("写入字节数:" + total);
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
