package com.zkc.example;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.Socket;
import java.util.Date;

@Slf4j
public class IOClient {
	
	public static void main(String[] args) throws IOException {
		//接收新连接线程
		new Thread(() -> {
			try {
				Socket socket = new Socket("127.0.0.1", 8000);
				while (true) {
					try {
						socket.getOutputStream().write((new Date() + ": hello world").getBytes());
						Thread.sleep(2000);
					} catch (Exception e) {
						log.error("client error ", e);
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}).start();
	}
}
