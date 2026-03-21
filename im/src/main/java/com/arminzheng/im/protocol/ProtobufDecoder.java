package com.arminzheng.im.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class ProtobufDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        // Mark read index
        in.markReaderIndex();

        // Check header length
        if (in.readableBytes() < 4) {
            return;
        }

        // Read Length
        int length = in.readUnsignedShort();
        if (length == 0) {
            ctx.close();
            return;
        }

        // Check Magic
        int magic = in.readUnsignedShort();
        if (magic != 0xFEFE) {
             throw new IllegalArgumentException("Magic number is wrong: " + magic);
        }

        // Check content length
        if (in.readableBytes() < length) {
            // Reset read index for next read
            in.resetReaderIndex();
            return;
        }

        // Read Content
        byte[] bytes = new byte[length];
        in.readBytes(bytes);

        // Deserialize
        ProtoMsg.Message message = ProtoMsg.Message.parseFrom(bytes);
        if (message != null) {
            out.add(message);
        }
    }
}
