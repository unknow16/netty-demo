package com.fuyi.netty.attr;

import io.netty.util.AttributeKey;

public class AttributeMapConstant {

    public static final AttributeKey<SessionInfo> SESSION_INFO_KEY = AttributeKey.valueOf("session.info");
    public static final AttributeKey<SessionInfo> SESSION2_INFO_KEY = AttributeKey.valueOf("session2.info");
}
