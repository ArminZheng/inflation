package com.arminzheng.im.session;

import io.netty.channel.Channel;
import io.netty.util.AttributeKey;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SessionUtil {
    private static final Map<String, Channel> USER_CHANNEL_MAP = new ConcurrentHashMap<>();

    private static final AttributeKey<Session> SESSION = AttributeKey.newInstance("session");

    private SessionUtil() {
    }

    public static void bindSession(Session session, Channel channel) {
        USER_CHANNEL_MAP.put(session.getUserId(), channel);
        channel.attr(SESSION).set(session);
    }

    public static void unbindSession(Channel channel) {
        if (hasLogin(channel)) {
            Session session = getSession(channel);
            USER_CHANNEL_MAP.remove(session.getUserId());
            channel.attr(SESSION).set(null);
            System.out.println(session + " 退出登录!");
        }
    }

    public static boolean hasLogin(Channel channel) {
        return getSession(channel) != null;
    }

    public static Session getSession(Channel channel) {
        return channel.attr(SESSION).get();
    }

    public static Channel getChannel(String userId) {
        return USER_CHANNEL_MAP.get(userId);
    }
}
