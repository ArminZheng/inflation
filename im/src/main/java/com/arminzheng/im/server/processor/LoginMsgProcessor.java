package com.arminzheng.im.server.processor;

import com.arminzheng.im.protocol.ProtoMsg;
import com.arminzheng.im.protocol.ProtoMsgFactory;
import com.arminzheng.im.session.Session;
import com.arminzheng.im.session.SessionUtil;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class LoginMsgProcessor {

    public ProtoMsg.Message process(ProtoMsg.Message msg, Channel channel) {
        ProtoMsg.LoginRequest loginRequest = msg.getLoginRequest();
        String uid = loginRequest.getUid();
        String token = loginRequest.getToken();

        log.info("处理登录业务逻辑, uid: {}", uid);

        boolean success = checkAuth(uid, token);

        if (success) {
            SessionUtil.bindSession(new Session(uid, uid), channel);
        }

        return ProtoMsgFactory.buildLoginResponse(msg, success, uid);
    }

    private boolean checkAuth(String uid, String token) {
        return uid != null && !uid.isBlank() && token != null && !token.isBlank();
    }
}
