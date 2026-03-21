package com.arminzheng.im.client.sender;

import com.arminzheng.im.client.ClientSessionContext;
import com.arminzheng.im.protocol.ProtoMsg;
import com.arminzheng.im.protocol.ProtoMsgFactory;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class LoginSender {
    private static final int LOGIN_MSG_ID = 1;

    public void sendLoginRequest(Channel channel, String userId, String password) {
        if (channel == null || !channel.isActive()) {
            log.warn("Channel 未连接或未绑定");
            return;
        }
        log.info("发送登录请求，userId: {}", userId);
        ProtoMsg.Message message = ProtoMsgFactory.buildLoginRequest(userId, password, LOGIN_MSG_ID);
        ClientSessionContext.cacheUserId(channel, userId);
        channel.writeAndFlush(message);
    }
}
