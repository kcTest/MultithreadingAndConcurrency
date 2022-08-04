package com.zkc.atomic;

import java.io.Serializable;

public class User implements Serializable {
	
	private String uid;
	private String nickName;
	public volatile int age;
	
	public User(String uid, String nickName) {
		this.uid = uid;
		this.nickName = nickName;
	}
	
	@Override
	public String toString() {
		return "User{" +
				"uid='" + getUid() + '\'' +
				", nickName='" + getNickName() + '\'' +
				", age=" + age +
				'}';
	}
	
	public String getUid() {
		return uid;
	}
	
	public String getNickName() {
		return nickName;
	}
	
}
