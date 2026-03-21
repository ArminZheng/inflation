package com.arminzheng.im.server.handler;

import com.arminzheng.im.protocol.ProtoMsg.HeadType;
import com.arminzheng.im.protocol.ProtoMsg;
import com.arminzheng.im.server.processor.LoginMsgProcessor;
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
public class UserLoginHandler extends SimpleChannelInboundHandler<ProtoMsg.Message> {

    @Autowired
    private LoginMsgProcessor loginMsgProcessor;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ProtoMsg.Message msg) throws Exception {
        if (msg.getType() != HeadType.LOGIN_REQUEST) {
            ctx.fireChannelRead(msg);
            return;
        }
        log.info("收到登录请求: {}", msg);
        ProtoMsg.Message response = loginMsgProcessor.process(msg, ctx.channel());
        ctx.channel().writeAndFlush(response);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        SessionUtil.unbindSession(ctx.channel());
        super.channelInactive(ctx);
    }
}
