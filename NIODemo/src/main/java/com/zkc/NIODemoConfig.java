package com.zkc;

import com.zkc.util.ConfigProperties;

public class NIODemoConfig extends ConfigProperties {
	
	private static ConfigProperties singleton = new NIODemoConfig("system.properties");
	
	public static final String SRC_FILENAME_COPY = singleton.getValue("src.filename.copy");
	public static final String DEST_FILENAME_COPY = singleton.getValue("dest.filename.copy");
	
	public static final String SRC_FILENAME_SOCKET_SEND = singleton.getValue("src.filename.socket.send");
	public static final String DEST_FILENAME_SOCKET_RECEIVE = singleton.getValue("dest.filename.socket.receive");
	public static final String SOCKET_SERVER_IP = singleton.getValue("socket.server.ip");
	public static final int SOCKET_SERVER_PORT = singleton.getIntValue("socket.server.port");
	public static final int SEND_BUFFER_SIZE = singleton.getIntValue("send.buffer.size");
	
	
	public NIODemoConfig(String fileName) {
		super(fileName);
		super.loadFromFile();
	}
	
}
