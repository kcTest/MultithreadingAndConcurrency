package com.zkc.nio;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * 多级目录复制
 */
public class DirectoryTranverseDemo01 {
	
	public static void main(String[] args) {
		
		try {
			String fromDir = "E:\\360RecycleBin\\5";
			String toDir = "E:\\360RecycleBin\\5-Test";
			
			Files.walk(Paths.get(fromDir)).forEach(path -> {
				//新目录或者新文件的位置  只用新目录替换当前子文件或子目录路径中的原始部分
				String newPathStr = path.toString().replace(fromDir, toDir);
				try {
					if (Files.isDirectory(path)) {
						Files.createDirectory(Paths.get(newPathStr));
					} else if (Files.isRegularFile(path)) {
						Files.copy(path, Paths.get(newPathStr));
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
