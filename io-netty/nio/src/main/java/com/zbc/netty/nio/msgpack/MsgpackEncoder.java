package com.zbc.netty.nio.msgpack;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.msgpack.jackson.dataformat.MessagePackFactory;

public class MsgpackEncoder extends MessageToByteEncoder<Object> {

    private final ObjectMapper mapper = new ObjectMapper(new MessagePackFactory());

    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        byte[] buf = mapper.writeValueAsBytes(msg);
        out.writeBytes(buf);
    }
}
