package com.zkc.chat.attribute;

import com.zkc.chat.client.session.Session;
import io.netty.util.AttributeKey;

public interface Attributes {
	AttributeKey<Session> SESSION = AttributeKey.newInstance("session");
}
