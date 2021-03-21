package com.zbc.netty.nio.msgpack;

import com.zbc.netty.nio.EchoServerHandler;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MsgpackServerHandler extends ChannelHandlerAdapter {

    private final Logger log = LoggerFactory.getLogger(EchoServerHandler.class);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ObjectBean oBean = (ObjectBean) msg;
        log.debug("bean msg: " + oBean);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
