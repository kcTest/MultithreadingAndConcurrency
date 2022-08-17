package com.zkc.chat.client.session;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户当前的会话信息
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Session {
	
	private String userId;
	private String userName;
	
	@Override
	public String toString() {
		return String.format("[userId='%s', userName='%s']", userId, userName);
	}
}
