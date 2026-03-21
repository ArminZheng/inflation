package com.arminzheng.im.server.processor;

import com.arminzheng.im.protocol.ProtoMsg;
import com.arminzheng.im.protocol.ProtoMsgFactory;
import com.arminzheng.im.session.SessionUtil;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ChatRedirectProcessor {

    public ProtoMsg.Message process(ProtoMsg.Message msg) {
        ProtoMsg.MessageRequest messageRequest = msg.getMessageRequest();
        String toUserId = messageRequest.getTo();
        String content = messageRequest.getContent();

        log.info("转发消息 to: {}", toUserId);

        if (toUserId == null || toUserId.isBlank() || content == null || content.isBlank()) {
            return ProtoMsgFactory.buildInvalidMessageResponse(msg, "Invalid Chat Request");
        }

        Channel toChannel = SessionUtil.getChannel(toUserId);

        if (toChannel != null && toChannel.isActive() && SessionUtil.hasLogin(toChannel)) {
            ProtoMsg.Message notification = ProtoMsgFactory.buildMessageNotification(msg);
            toChannel.writeAndFlush(notification);
            return ProtoMsgFactory.buildMessageResponse(msg, true, "Message Delivered");
        }

        log.warn("用户 {} 不在线，消息转存离线", toUserId);
        return ProtoMsgFactory.buildMessageResponse(msg, false, "Target User Offline");
    }
}
