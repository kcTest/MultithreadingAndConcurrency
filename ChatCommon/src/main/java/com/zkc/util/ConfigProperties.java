package com.zkc.util;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public class ConfigProperties {
	
	private final String propertyFileName;
	private final Properties properties = new Properties();
	
	
	public ConfigProperties(String propertyFileName) {
		this.propertyFileName = propertyFileName;
	}
	
	protected void loadFromFile() {
		InputStreamReader iReader = null;
		try {
			String filePath = IOUtil.getResourcePath(propertyFileName);
			InputStream is = new FileInputStream(filePath);
			iReader = new InputStreamReader(is, StandardCharsets.UTF_8);
			properties.load(iReader);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			IOUtil.closeQuietly(iReader);
		}
	}
	
	public String getValue(String key) {
		return readProperty(key);
	}
	
	private String readProperty(String key) {
		return properties.getProperty(key);
	}
}
