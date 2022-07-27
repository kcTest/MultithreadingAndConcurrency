package com.zkc.util;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

public class IOUtil {
	
	/**
	 * 获取当前类路径下指定资源名称的完整路径
	 *
	 * @param resName 资源名称 以/开头会得到 /目录/资源名 ，不带/得到 包名.资源名
	 * @return 完整路径
	 */
	public static String getResourcePath(String resName) {
		URL url = IOUtil.class.getResource("/" + resName);
		String path = null;
		try {
			path = URLDecoder.decode(url.getPath(), StandardCharsets.UTF_8.name());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return path;
	}
	
	public static void closeQuietly(Closeable c) {
		if (c == null) {
			return;
		}
		try {
			c.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 构建指定名称的资源文件的完整路径
	 */
	public static String buildResourcePath(String resName) {
		URL url = IOUtil.class.getResource("/");
		String path = null;
		try {
			path = URLDecoder.decode(url.getPath(), "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return path + resName;
	}
}
