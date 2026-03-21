package com.arminzheng.im.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

@ChannelHandler.Sharable
public class ProtobufEncoder extends MessageToByteEncoder<ProtoMsg.Message> {

    @Override
    protected void encode(ChannelHandlerContext ctx, ProtoMsg.Message msg, ByteBuf out) throws Exception {
        byte[] bytes = msg.toByteArray();
        int length = bytes.length;
        // Write Length
        out.writeShort(length);
        // Write Magic
        out.writeShort(0xFEFE);
        // Write Content
        out.writeBytes(bytes);
    }
}
