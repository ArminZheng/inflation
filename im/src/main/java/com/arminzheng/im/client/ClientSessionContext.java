package com.arminzheng.im.client;

import io.netty.channel.Channel;
import io.netty.util.AttributeKey;

public final class ClientSessionContext {
    private static final AttributeKey<String> USER_ID_KEY = AttributeKey.valueOf("userId");

    private ClientSessionContext() {
    }

    public static void cacheUserId(Channel channel, String userId) {
        channel.attr(USER_ID_KEY).set(userId);
    }

    public static String getCachedUserId(Channel channel) {
        return channel.attr(USER_ID_KEY).get();
    }
}
