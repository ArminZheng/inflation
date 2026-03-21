package com.arminzheng.im.client.handler;

import com.arminzheng.im.protocol.ProtoMsg;
import com.arminzheng.im.protocol.ProtoMsg.HeadType;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ChatMsgHandler extends SimpleChannelInboundHandler<ProtoMsg.Message> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ProtoMsg.Message msg) throws Exception {
        if (msg.getType() != HeadType.MESSAGE_NOTIFICATION) {
            ctx.fireChannelRead(msg);
            return;
        }

        ProtoMsg.MessageNotification notification = msg.getMessageNotification();
        log.info("收到消息 [{}]: {}", notification.getSender(), notification.getContent());
    }
}
