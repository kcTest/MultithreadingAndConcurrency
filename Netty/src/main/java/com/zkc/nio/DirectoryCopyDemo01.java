package com.zkc.nio;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 遍历文件夹
 */
public class DirectoryCopyDemo01 {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ScatteringReadDemo.class);
	
	public static void main(String[] args) {
		AtomicInteger dirCount = new AtomicInteger();
		AtomicInteger fileCount = new AtomicInteger();
		AtomicInteger jarFileCount = new AtomicInteger();
		
		try {
			//visitor
			Files.walkFileTree(Paths.get("D:\\Java\\jdk1.8.0_301"), new SimpleFileVisitor<Path>() {
				
				@Override
				public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
					LOGGER.debug("===>" + dir);
					dirCount.incrementAndGet();
					return super.preVisitDirectory(dir, attrs);
				}
				
				@Override
				public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
					LOGGER.debug("-----" + file);
					fileCount.incrementAndGet();
					if (file.toString().endsWith("jar")) {
						jarFileCount.incrementAndGet();
					}
					return super.visitFile(file, attrs);
				}
			});
			
			LOGGER.debug("dirCount:" + dirCount + " fileCount:" + fileCount + " jarFileCount:" + jarFileCount);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
