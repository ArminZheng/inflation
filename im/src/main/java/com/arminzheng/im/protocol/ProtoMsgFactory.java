package com.arminzheng.im.protocol;

import com.arminzheng.im.protocol.ProtoMsg.HeadType;

public final class ProtoMsgFactory {
    private static final int LOGIN_SUCCESS_CODE = 200;
    private static final int AUTH_FAILED_CODE = 401;
    private static final int CHAT_SUCCESS_CODE = 200;
    private static final int TARGET_OFFLINE_CODE = 404;
    private static final int INVALID_REQUEST_CODE = 400;

    private ProtoMsgFactory() {
    }

    public static ProtoMsg.Message buildLoginRequest(String userId, String password, int msgId) {
        ProtoMsg.LoginRequest loginRequest = ProtoMsg.LoginRequest.newBuilder()
                .setUid(userId)
                .setToken(password)
                .setPlatform(1)
                .build();

        return ProtoMsg.Message.newBuilder()
                .setType(HeadType.LOGIN_REQUEST)
                .setHead(buildHead(HeadType.LOGIN_REQUEST, msgId))
                .setLoginRequest(loginRequest)
                .build();
    }

    public static ProtoMsg.Message buildLoginResponse(ProtoMsg.Message request, boolean success, String sessionId) {
        ProtoMsg.LoginResponse loginResponse = ProtoMsg.LoginResponse.newBuilder()
                .setResult(success)
                .setCode(success ? LOGIN_SUCCESS_CODE : AUTH_FAILED_CODE)
                .setInfo(success ? "Login Success" : "Auth Failed")
                .build();

        return ProtoMsg.Message.newBuilder()
                .setType(HeadType.LOGIN_RESPONSE)
                .setSessionId(sessionId)
                .setHead(buildHead(HeadType.LOGIN_RESPONSE, request.getHead().getMsgId()))
                .setLoginResponse(loginResponse)
                .build();
    }

    public static ProtoMsg.Message buildChatRequest(String fromUserId, String toUserId, String content, int msgId) {
        ProtoMsg.MessageRequest messageRequest = ProtoMsg.MessageRequest.newBuilder()
                .setMsgId("M" + System.currentTimeMillis())
                .setFrom(fromUserId)
                .setTo(toUserId)
                .setTime(System.currentTimeMillis())
                .setMsgType(1)
                .setContent(content)
                .build();

        return ProtoMsg.Message.newBuilder()
                .setType(HeadType.MESSAGE_REQUEST)
                .setHead(buildHead(HeadType.MESSAGE_REQUEST, msgId))
                .setMessageRequest(messageRequest)
                .build();
    }

    public static ProtoMsg.Message buildMessageNotification(ProtoMsg.Message request) {
        ProtoMsg.MessageRequest messageRequest = request.getMessageRequest();
        ProtoMsg.MessageNotification notification = ProtoMsg.MessageNotification.newBuilder()
                .setMsgId(messageRequest.getMsgId())
                .setSender(messageRequest.getFrom())
                .setContent(messageRequest.getContent())
                .setMsgType(messageRequest.getMsgType())
                .setTimestamp(System.currentTimeMillis())
                .build();

        return ProtoMsg.Message.newBuilder()
                .setType(HeadType.MESSAGE_NOTIFICATION)
                .setSessionId(messageRequest.getTo())
                .setHead(buildHead(HeadType.MESSAGE_NOTIFICATION, request.getHead().getMsgId()))
                .setMessageNotification(notification)
                .build();
    }

    public static ProtoMsg.Message buildMessageResponse(ProtoMsg.Message request, boolean success, String info) {
        int code = success ? CHAT_SUCCESS_CODE : TARGET_OFFLINE_CODE;
        return buildMessageResponse(request, success, code, info);
    }

    public static ProtoMsg.Message buildInvalidMessageResponse(ProtoMsg.Message request, String info) {
        return buildMessageResponse(request, false, INVALID_REQUEST_CODE, info);
    }

    private static ProtoMsg.Message buildMessageResponse(ProtoMsg.Message request,
                                                         boolean success,
                                                         int code,
                                                         String info) {
        ProtoMsg.MessageRequest messageRequest = request.getMessageRequest();
        ProtoMsg.MessageResponse response = ProtoMsg.MessageResponse.newBuilder()
                .setResult(success)
                .setCode(code)
                .setInfo(info)
                .setLastBlock(true)
                .setBlockIndex(1L)
                .build();

        return ProtoMsg.Message.newBuilder()
                .setType(HeadType.MESSAGE_RESPONSE)
                .setSessionId(messageRequest.getFrom())
                .setHead(buildHead(HeadType.MESSAGE_RESPONSE, request.getHead().getMsgId()))
                .setMessageResponse(response)
                .build();
    }

    private static ProtoMsg.Head buildHead(HeadType type, int msgId) {
        return ProtoMsg.Head.newBuilder()
                .setMsgType(type)
                .setMsgId(msgId)
                .setTimestamp(System.currentTimeMillis())
                .build();
    }
}
