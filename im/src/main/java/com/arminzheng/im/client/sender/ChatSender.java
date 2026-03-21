package com.arminzheng.im.client.sender;

import com.arminzheng.im.protocol.ProtoMsg;
import com.arminzheng.im.protocol.ProtoMsgFactory;
import com.arminzheng.im.session.Session;
import com.arminzheng.im.session.SessionUtil;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ChatSender {
    private static final int CHAT_MSG_ID = 2;

    public void sendChatRequest(Channel channel, String toUserId, String content) {
        if (channel == null || !channel.isActive()) {
            log.warn("Channel 未连接或未绑定");
            return;
        }
        Session session = SessionUtil.getSession(channel);
        if (session == null) {
            log.warn("用户未登录");
            return;
        }
        String fromUserId = session.getUserId();
        log.info("发送聊天请求，from: {}, to: {}, content: {}", fromUserId, toUserId, content);
        ProtoMsg.Message message = ProtoMsgFactory.buildChatRequest(fromUserId, toUserId, content, CHAT_MSG_ID);
        channel.writeAndFlush(message);
    }
}
