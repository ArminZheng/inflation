package com.arminzheng.im.client.handler;

import com.arminzheng.im.client.ClientSessionContext;
import com.arminzheng.im.protocol.ProtoMsg;
import com.arminzheng.im.protocol.ProtoMsg.HeadType;
import com.arminzheng.im.session.Session;
import com.arminzheng.im.session.SessionUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LoginResponseHandler extends SimpleChannelInboundHandler<ProtoMsg.Message> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ProtoMsg.Message msg) throws Exception {
        if (msg.getType() != HeadType.LOGIN_RESPONSE) {
            ctx.fireChannelRead(msg);
            return;
        }

        ProtoMsg.LoginResponse loginResponse = msg.getLoginResponse();
        if (loginResponse.getResult()) {
            log.info("登录成功: {}", loginResponse.getInfo());
            String uid = ClientSessionContext.getCachedUserId(ctx.channel());
            if (uid == null || uid.isBlank()) {
                uid = msg.getSessionId();
            }
            if (uid != null && !uid.isBlank()) {
                SessionUtil.bindSession(new Session(uid, uid), ctx.channel());
            }
        } else {
            log.error("登录失败: {}", loginResponse.getInfo());
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        SessionUtil.unbindSession(ctx.channel());
        super.channelInactive(ctx);
    }
}
