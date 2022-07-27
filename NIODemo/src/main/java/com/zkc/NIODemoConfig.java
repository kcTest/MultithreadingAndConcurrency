package com.zkc;

import com.zkc.util.ConfigProperties;

public class NIODemoConfig extends ConfigProperties {
	
	private static ConfigProperties singleton = new NIODemoConfig("system.properties");
	
	public static final String FILE_SRC_RESOURCE_NAME = singleton.getValue("file.resource.src.path");
	public static final String FILE_DEST_RESOURCE_NAME = singleton.getValue("file.resource.dest.path");
	
	public NIODemoConfig(String fileName) {
		super(fileName);
		super.loadFromFile();
	}
	
}
