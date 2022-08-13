package com.zkc.nio;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * 遍历文件夹 先删除文件 再删除文件夹
 */
public class DirectoryTranverseDemo02 {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ScatteringReadDemo.class);
	
	public static void main(String[] args) {
		
		try {
			Files.walkFileTree(Paths.get("E:\\QQMusicCache\\Log"), new SimpleFileVisitor<Path>() {
				@Override
				public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
					LOGGER.debug("===>" + dir);
					return super.preVisitDirectory(dir, attrs);
				}
				
				@Override
				public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
					LOGGER.debug("delete-----" + file);
					Files.delete(file);
					return super.visitFile(file, attrs);
				}
				
				@Override
				public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
					LOGGER.debug("delete====" + dir);
					Files.delete(dir);
					return super.postVisitDirectory(dir, exc);
				}
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
