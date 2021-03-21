package com.zbc.netty.nio.msgpack;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import org.msgpack.jackson.dataformat.MessagePackFactory;

import java.util.List;

public class MsgpackDecoder extends MessageToMessageDecoder<ByteBuf> {

    private final ObjectMapper mapper = new ObjectMapper(new MessagePackFactory());

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
        byte[] buf = new byte[msg.readableBytes()];
        msg.getBytes(msg.readerIndex(), buf, 0, buf.length);
        out.add(mapper.readValue(buf, ObjectBean.class));
    }
}
