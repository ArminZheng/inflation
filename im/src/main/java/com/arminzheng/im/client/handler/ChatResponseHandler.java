package com.arminzheng.im.client.handler;

import com.arminzheng.im.protocol.ProtoMsg;
import com.arminzheng.im.protocol.ProtoMsg.HeadType;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ChatResponseHandler extends SimpleChannelInboundHandler<ProtoMsg.Message> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ProtoMsg.Message msg) throws Exception {
        if (msg.getType() != HeadType.MESSAGE_RESPONSE) {
            ctx.fireChannelRead(msg);
            return;
        }

        ProtoMsg.MessageResponse response = msg.getMessageResponse();
        if (response.getResult()) {
            log.info("消息发送成功: {}", response.getInfo());
            return;
        }

        log.warn("消息发送失败: {}", response.getInfo());
    }
}
