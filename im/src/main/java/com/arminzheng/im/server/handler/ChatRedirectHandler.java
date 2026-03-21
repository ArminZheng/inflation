package com.arminzheng.im.server.handler;

import com.arminzheng.im.protocol.ProtoMsg;
import com.arminzheng.im.protocol.ProtoMsg.HeadType;
import com.arminzheng.im.protocol.ProtoMsgFactory;
import com.arminzheng.im.server.processor.ChatRedirectProcessor;
import com.arminzheng.im.session.SessionUtil;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@ChannelHandler.Sharable
public class ChatRedirectHandler extends SimpleChannelInboundHandler<ProtoMsg.Message> {

    @Autowired
    private ChatRedirectProcessor chatRedirectProcessor;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ProtoMsg.Message msg) throws Exception {
        if (msg.getType() != HeadType.MESSAGE_REQUEST) {
            ctx.fireChannelRead(msg);
            return;
        }

        if (!SessionUtil.hasLogin(ctx.channel())) {
            log.warn("用户未登录，不能发送消息");
            ctx.channel().writeAndFlush(ProtoMsgFactory.buildInvalidMessageResponse(msg, "User Not Logged In"));
            return;
        }

        ProtoMsg.Message response = chatRedirectProcessor.process(msg);
        ctx.channel().writeAndFlush(response);
    }
}
